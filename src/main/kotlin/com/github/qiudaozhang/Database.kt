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
package com.github.qiudaozhang

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * 数据库模型
 * @author qiudaozhang
 * 2023-9-22
 *
 */
data class Database(
    val fromTables: List<Table>, // 起点数据表集合 ，一般是自己的测试数据库
    val toTables: List<Table>, // 目标数据表集合  ， 一般是生产数据库
    val output: String,//输出目录
) {


    /**
     * 计算需要处理的新表
     *
     */
    fun newTables(): List<Table> {
//        val fromNames = fromTables.map { it.name }
//        return toTables.filter { !fromNames.contains(it.name) }
        val toNames = toTables.map { it.name }
        return fromTables.filter { !toNames.contains(it.name) }
    }


    fun newTableScript() {
        val tables = newTables()
        val f = File(output + File.separator + "new_tables.sql")
        if (!f.parentFile.exists()) {
            f.parentFile.mkdirs()
        }
        if (!f.exists()) {
            f.createNewFile()
        } else {
            f.delete()
            f.createNewFile()
        }
        var write: BufferedWriter? = null
        try {
            write = BufferedWriter(FileWriter(f))
            tables.forEach {
                val sql = it.newTable()
                write.write(sql)
                write.newLine()
            }
        } catch (e: IOException) {

        } finally {
            write?.let {
                write.close()
            }
        }
    }

    fun changeTableScript() {
        val tables = changeTables()
        val sqls = mutableListOf<String>()
        tables.forEach { (from, to) ->
            sqls.addAll(from.changeTableScript(to))
        }
        val f = File(output + File.separator + "change_tables.sql")
        if (!f.parentFile.exists()) {
            f.parentFile.mkdirs()
        }
        if (!f.exists()) {
            f.createNewFile()
        }
        var write: BufferedWriter? = null
        try {
            write = BufferedWriter(FileWriter(f))
            println(sqls.size)
            sqls.forEach { sql ->
                write.write(sql)
                write.newLine()
            }
        } catch (e: IOException) {

        } finally {
            write?.let {
                write.close()
            }
        }
    }

    /**
     * 计算有变动的表
     *
     * @return
     */
    fun changeTables(): List<Pair<Table, Table>> {
        // 先找出交集
        val fromNames = fromTables.map { it.name }
        val sameTables = toTables.filter { fromNames.contains(it.name) }
        val tables = mutableListOf<Pair<Table, Table>>()
        sameTables.forEach { st ->
            val fromTable = fromTables.find { it.name == st.name }!!
            if (fromTable.name == st.name) {
                // 能自动处理的表
                if (fromTable.hasChange(st)) {
                    if (fromTable.canAutoHandle(st)) {
                        tables.add(Pair(fromTable, st))
                    }
                }

            }
        }
        return tables
    }

    /**
     * 需要手动处理的表
     *
     * @return
     */
    fun manualTables(): List<Pair<Table, Table>> {
        val fromNames = fromTables.map { it.name }
        val sameTables = toTables.filter { fromNames.contains(it.name) }
        val tables = mutableListOf<Pair<Table, Table>>()
        sameTables.forEach { st ->
            val fromTable = fromTables.find { it.name == st.name }!!
            if (fromTable == st) {
                // 能自动处理的表
                if (!fromTable.canAutoHandle(st)) {
                    tables.add(Pair(fromTable, st))
                }
            }
        }
        return tables
    }
}























