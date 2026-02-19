Class guard.WeeklyVectorGenerator
{

ClassMethod run(windowDays As %Integer = 90) [ Language = python ]
{
    import iris
    from datetime import date, timedelta

    today = date.today()
    from_date = today - timedelta(days=windowDays)

    sql = """
        SELECT
            GlobalName,
            Location,

            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 2 THEN GrowthMB ELSE 0 END) AS Mon,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 3 THEN GrowthMB ELSE 0 END) AS Tue,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 4 THEN GrowthMB ELSE 0 END) AS Wed,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 5 THEN GrowthMB ELSE 0 END) AS Thu,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 6 THEN GrowthMB ELSE 0 END) AS Fri,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 7 THEN GrowthMB ELSE 0 END) AS Sat,
            AVG(CASE WHEN DAYOFWEEK(SnapshotDate) = 1 THEN GrowthMB ELSE 0 END) AS Sun

        FROM guard.GlobalSnapshot
        WHERE
            GrowthMB IS NOT NULL
            AND SnapshotDate >= ?
        GROUP BY GlobalName, Location
    """
    print(f"Running WeeklyVectorGenerator for data from {from_date} to {today} (window: {windowDays} days)")
    select_stmt = iris.sql.prepare(sql)
    rs = select_stmt.execute(from_date)

    print(rs)

    insert_stmt = iris.sql.prepare("""
            INSERT INTO guard.GlobalGrowthProfile
            (GlobalName, Location, WindowDays, FromDate, ToDate, WeeklyVector,
                AVGMon,
                AVGTue,
                AVGWed,
                AVGThu,
                AVGFri,
                AVGSat,
                AVGSun)
            VALUES (?, ?, ?, ?, ?, TO_VECTOR(?, DOUBLE), ?, ?, ?, ?, ?, ?, ?)
    """)

    for idx, row in enumerate(rs):
    
        print(f"Processing Global: {row[0]}, Location: {row[1]}")

        global_name = row[0]
        location = row[1]

        values = [
            float(row[2] or 0),
            float(row[3] or 0),
            float(row[4] or 0),
            float(row[5] or 0),
            float(row[6] or 0),
            float(row[7] or 0),
            float(row[8] or 0),
        ]

        total = sum(values)

        if total <= 0:
            continue

        normalized = [v / total for v in values]

        vector_str = "[" + ",".join(f"{v:.6f}" for v in normalized) + "]"
        print(f"  Weekly Vector: {vector_str}")
        print(f"  Averages: Mon={row[2]}, Tue={row[3]}, Wed={row[4]}, Thu={row[5]}, Fri={row[6]}, Sat={row[7]}, Sun={row[8]}")
        insert_stmt.execute(
            global_name,
            location,
            str(windowDays),
            str(from_date),
            str(today),
            vector_str,
            float(row[2] or 0),
            float(row[3] or 0),
            float(row[4] or 0),
            float(row[5] or 0),
            float(row[6] or 0),
            float(row[7] or 0),
            float(row[8] or 0)
        )
}

}
