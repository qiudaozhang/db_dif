/*
 * Copyright 2023-2023 qiudaozhang(https://github.com/qiudaozhang)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.qiudaozhang.model

import org.apache.commons.dbutils.DbUtils
import java.sql.Connection
import java.sql.DriverManager

/**
 * @author qiudaozhang
 * db 解析器
 *
 */
class DbParser {

    var username: String? = null
    var password: String? = null
    var driverName: String? = null
    var url: String? = null
    var dbName: String? = null


    constructor(username: String?, password: String?, driverName: String?, url: String?, dbName: String?) {
        this.username = username
        this.password = password
        this.driverName = driverName
        this.url = url
        this.dbName = dbName
        DbUtils.loadDriver(driverName)
    }


    fun ok(): Boolean {

        var conn:Connection?=null
        try {
            conn = getConnection()
            // 还要检查是否有这个数据库
            val logs = conn.metaData.catalogs
            while (logs.next()){
                val databaseName: String = logs.getString("TABLE_CAT")
                if(databaseName == dbName){
                    return true
                }

            }
            return false
        } catch (e: Exception) {
            return false
        } finally {
            conn?.let{
                DbUtils.close(conn)
            }
        }

    }


    fun getConnection(): Connection {
        return DriverManager.getConnection(url, username, password)
    }


    fun scanAllTable(): List<Table> {

        val names = getAllTableNames()
        val tables = mutableListOf<Table>()
        names.forEach {
            val tb = getTableInfo(it)
            tables.add(tb)
        }
        return tables
    }


    fun getAllTableNames(): List<String> {
        val conn = getConnection()
        val tables = mutableListOf<String>()
        try {
            val metaData = conn.metaData
            val rs = metaData.getTables(dbName, null, null, listOf("TABLE").toTypedArray())

            while (rs.next()) {
                val tableName = rs.getString("TABLE_NAME")
                tables.add(tableName)
            }
        } finally {
            DbUtils.close(conn)
        }
        return tables
    }


    fun getTableInfo(tableName: String): Table {
//        println("=======================TABLE : ${tableName} ============================")
        val conn = getConnection()
        val columns = mutableListOf<Column>()
        val metaData = conn.metaData
        val tbs = metaData.getTables(dbName, null, tableName, null)
        var tableComment = ""
        while (tbs.next()) {
            tableComment = tbs.getString("REMARKS")
        }
        val table = Table(name = tableName, columns = columns, comment = tableComment)
        try {
            val rs = metaData.getColumns(dbName, null, tableName, null)
            val pkRs = metaData.getPrimaryKeys(null, null, tableName)
            val primaryKeys = mutableListOf<String>()
            while (pkRs.next()) {
                val cn = pkRs.getString("COLUMN_NAME")
                primaryKeys.add(cn)
            }
            while (rs.next()) {
                val name = rs.getString("COLUMN_NAME")
                val type = rs.getString("TYPE_NAME")
                val defaultValue = rs.getString("COLUMN_DEF")
                val nullable = rs.getInt("NULLABLE") == 1
                var size = rs.getString("COLUMN_SIZE").toInt()
                val comment = rs.getString("REMARKS")
                var digital = 0
                if (type == "DECIMAL") {
                    digital = rs.getInt("DECIMAL_DIGITS")
                }
                if (type == "DATETIME") {
                    size = 0
                }

                val f = primaryKeys.find { it == name }
                val pk = f != null
                val column = Column(
                    name,
                    type,
                    size,
                    nullable = nullable,
                    digital = digital,
                    defaultValue = defaultValue,
                    comment = comment,
                    primary = pk
                )
                columns.add(column)
            }


        } finally {
            DbUtils.close(conn)
        }
        return table
    }


}