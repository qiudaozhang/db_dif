package com.github.qiudaozhang

import com.github.qiudaozhang.model.Database
import com.github.qiudaozhang.model.DbParser
import org.junit.jupiter.api.Test
import java.io.File

class TestScanTable {


    val fromUsername = "xx"
    val fromPassword = "xx"
    val fromDbName = "xx"
    val fromUrl = "jdbc:mysql://localhost:3306/xx"
    val fromDriverName = "com.mysql.cj.jdbc.Driver"

    val toUsername = "xx"
    val toPassword = "xx"
    val toDbName = "xx"
    val toUrl = "jdbc:mysql://localhost:3306/xx"
    val toDriverName = "com.mysql.cj.jdbc.Driver"

    @Test
    fun scanTable() {
        val dp = DbParser(fromUsername, fromPassword, fromDriverName, fromUrl, fromDbName)
        val tables = dp.getAllTableNames()

        tables.forEach {
            val info = dp.getTableInfo(it)
            val sql = info.newTable()
            println(sql)
        }
    }


    @Test
    fun execute() {
        val fromDp = DbParser(fromUsername, fromPassword, fromDriverName, fromUrl, fromDbName)
        val toDp = DbParser(toUsername, toPassword, toPassword, toUrl, toDbName)
        val fromTables = fromDp.scanAllTable()
        val toTables = toDp.scanAllTable()
        val home = System.getProperty("user.home")
        val database = Database(fromTables, toTables,home +File.separator + "db_dif")
        database.newTableScript()
        database.changeTableScript()

    }

}