Class guard.SnapshotGenerator
{

ClassMethod run() [ Language = python ]
{
    import iris

    directories = iris.cls("guard.SnapshotGenerator").listAllDirectories()
    for dir in directories:
        list_of_globals = iris.cls("guard.SnapshotGenerator").listGlobals(dir)
        for global_info in list_of_globals:
            try:
                insert_stmt = iris.sql.prepare("""
                    INSERT INTO guard.GlobalSnapshot (SnapshotDate, GlobalName, Location, AllocatedMB, UsedMB, Tables, PrevSnapshotId, GrowthMB, GrowthPct)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)
                insert_stmt.execute(
                    global_info["SnapshotDate"].isoformat(),
                    global_info["GlobalName"],
                    global_info["Location"],
                    str(global_info["AllocatedMB"]),
                    str(global_info["UsedMB"]),
                    ",".join(global_info["Tables"]),
                    global_info["PrevSnapshotId"] if global_info["PrevSnapshotId"] is not None else "",
                    str(global_info["GrowthMB"]) if global_info["GrowthMB"] is not None else "",
                    str(global_info["GrowthPct"]) if global_info["GrowthPct"] is not None else ""
                )

            except Exception as ex:
                print(ex)
                
        print(f"Completed snapshot for directory: {dir}")
}

ClassMethod listAllDirectories() As %List [ Language = python ]
{
    import iris

    list_of_directories = []
    stmt = iris.sql.prepare("SELECT DISTINCT %EXACT(Directory) FROM Config.Databases WHERE SectionHeader = 'Databases'")
    rs = stmt.execute()
    for idx, row in enumerate(rs):
        list_of_directories.append(row[0])

    return list_of_directories
}

ClassMethod listGlobals(directory As %String) As %List [ Language = python ]
{
    import iris
    import datetime
    from decimal import Decimal

    list_of_globals = []
    
    query = "SELECT Name,\"Allocated MB\",\"Used MB\" FROM %SYS.GlobalQuery_Size(?, '', '*','2') "
    stmt = iris.sql.prepare(query)
    rs = stmt.execute(directory)
    for idx, row in enumerate(rs):
        global_name = row[0]

        global_info = {
            "GlobalName": global_name,
            "SnapshotDate": datetime.date.today(),
            "Location": directory,
            "AllocatedMB": Decimal(row[1]),
            "UsedMB": Decimal(row[2]),
            "Tables": [],

            "PrevSnapshotId": None,
            "GrowthMB": None,
            "GrowthPct": None
        }

        if global_info["UsedMB"] == 0:
            continue

        global_info["Tables"] = iris.cls("guard.SnapshotGenerator").getMappedTables(global_name)
        
        global_info["PrevSnapshotId"] = iris.cls("guard.SnapshotGenerator").getPreviousSnapshotId(global_name, directory)
        if global_info["PrevSnapshotId"] is not None:
            prev_snapshot = iris.cls("guard.GlobalSnapshot")._OpenId(global_info["PrevSnapshotId"])
            global_info["GrowthMB"] = global_info["UsedMB"] - Decimal(prev_snapshot.UsedMB)
            if Decimal(prev_snapshot.UsedMB) > 0:
                global_info["GrowthPct"] = (global_info["GrowthMB"] / Decimal(prev_snapshot.UsedMB)) * Decimal(100)
    
        list_of_globals.append(global_info)

    return list_of_globals
}

ClassMethod getMappedTables(globalName As %String) As %Status [ Language = python ]
{
    import iris
    globalName = "^" + globalName
    query = """
        SELECT parent FROM %Dictionary.CompiledStorage
            WHERE DataLocation = ?
        UNION ALL
        SELECT parent FROM %Dictionary.CompiledStorage
            WHERE IdLocation = ?
        UNION ALL
        SELECT parent FROM %Dictionary.CompiledStorage
            WHERE IndexLocation = ?
        UNION ALL
        SELECT parent FROM %Dictionary.CompiledStorage
            WHERE StreamLocation = ?
    """
    stmt = iris.sql.prepare(query)
    rs = stmt.execute(globalName, globalName, globalName, globalName)
    tables = []
    for idx, row in enumerate(rs):
        if row[0] not in tables:
            tables.append(row[0])

    return tables
}

ClassMethod getPreviousSnapshotId(globalName As %String, directory As %String) As %Integer [ Language = python ]
{
    import iris
    query = "SELECT TOP 1 Id FROM guard.GlobalSnapshot WHERE GlobalName = ? AND Location = ? ORDER BY SnapshotDate DESC"
    stmt = iris.sql.prepare(query)
    rs = stmt.execute(globalName, directory)
    for idx, row in enumerate(rs):
        return row[0]
    return None
}

}
