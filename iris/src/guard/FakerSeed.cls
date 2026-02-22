Class quard.FakerSeed Extends %RegisteredObject
{

ClassMethod run()
{
    Set today = $ZDate($Horolog,3)
    Write "Completed initial snapshot with date: ", today, !

    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-10 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-11 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-12 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-13 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-14 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-15 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-16 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-17 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-18 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-19 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-20 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")
    do ##class(guard.SnapshotGenerator).run()
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET SnapshotDate = '2026-02-21 00:00:00' WHERE DATE(SnapshotDate) >=  DATE(NOW())")


    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET GlobalName = 'data.TblEventLogD', tables = 'data.TblEventLog' WHERE tables = 'guard.GlobalSnapshot' AND GlobalName = 'guard.GlobalSnapshotD'")
    Set rs = ##class(%SQL.Statement).%ExecDirect(,"UPDATE guard.GlobalSnapshot SET GlobalName = 'data.TblEventLogI', tables = 'data.TblEventLog' WHERE tables = 'guard.GlobalSnapshot' AND GlobalName = 'guard.GlobalSnapshotI'")

    Do ##class(guard.WeeklyVectorGenerator).run()
}

}
