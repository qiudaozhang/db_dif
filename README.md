# db_dif
数据库差异项目，避免上线数据库DDL不对导致bug

# 开发目标

- 能够自动处理新增的表
- 能够处理无冲突的表的变动
- 能够标记无法自动处理表，以便单独关注这些敏感表的变动


# 使用case


```kotlin 
package com.github.qiudaozhang

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
```