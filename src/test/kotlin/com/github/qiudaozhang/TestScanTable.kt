package com.github.qiudaozhang

import com.github.qiudaozhang.model.Database
import com.github.qiudaozhang.model.DbParser
import org.junit.jupiter.api.Test
import java.io.File

class TestScanTable {


    val fromUsername = "root"
    val fromPassword = "root"
    val fromDbName = "rent_from"
    val fromUrl = "jdbc:mysql://localhost:3306/rent_from"
    val fromDriverName = "com.mysql.cj.jdbc.Driver"

    val toUsername = "root"
    val toPassword = "root"
    val toDbName = "rent_to"
    val toUrl = "jdbc:mysql://localhost:3306/rent_to"
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