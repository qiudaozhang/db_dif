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

/**
 * 表定义
 * @author qiudaozhang
 * 2023-9-22
 */
data class Table(
    val name: String, // 表名
    val comment: String = "", // 表注释
    val columns: List<Column>, // 列的集合
    val dbType: DbType = DbType.MYSQL, // 数据库类型 比如 mysql （预留字段 将来支持更多的数据库，暂时只实现MYSQL）

) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Table) {
            return false
        }
        if (name != other.name) return false
        if (comment != other.comment) return false
        if (columns.size != other.columns.size) return false
        val zip = columns zip other.columns
        zip.forEach { (c1, c2) ->
            if (c1 != c2) {
                return false
            }
        }
        return false
    }

    /**
     * 当前表作为起始表
     * @param to 是目标表(需要修改的表)
     * 由起始表到目标表的SQL语句处理
     */
    fun difColumns(to: Table): DifColumn? {
        if (name != to.name) return null
        val newColumns = mutableListOf<Column>()
        val updateColumns = mutableListOf<Pair<Column, Column>>()
        columns.forEach { fromColumn ->
            // to的表如果有
            val toColumn = to.columns.find { it.name == fromColumn.name }
            // 要看他们是否相当
            if (toColumn == null) {
                // 目标表没有，属于新增的列
                newColumns.add(fromColumn)
            } else {
                // 目标表没有，看是否两列相等
                if (toColumn != fromColumn) {
                    // 需要修改的
                    updateColumns.add(Pair(fromColumn, toColumn))
                }
            }

        }
        return DifColumn(name, newColumns, updateColumns, emptyList())

    }


    /**
     * 是否能够自动处理
     * 比如原始t表有2列 a b
     * 目标表有3列     a c d
     * 也就是 原始表有列不在目标里面意味着可能有删除，也有可能是改名了，这种场景不允许自动处理，需要标记出来
     * 业务人员单独处理该表
     */
    fun canAutoHandle(other: Table): Boolean {
        columns.forEach { c ->
            val empty = other.columns.find { it.name == c.name }
            if (empty == null) {
                // 有新添加列
//                println("${name} 新增列")
            } else {
                if (c.type != empty.type) {
                    println("${name} 原始表字段 ${c.name} 类型发生了改变，无法自动处理")
                    return false
                }
            }
        }
        return true
    }

    fun hasChange(other: Table): Boolean {
        columns.forEach { c ->
            val empty = other.columns.find { it.name == c.name }
            if (empty == null) {
                // 有新添加列
                return true
            } else {
                if (c.type != empty.type) {
                    return true
                }
            }
        }
        return false
    }

    fun newTable(): String {
        val sqls = mutableListOf<String>()
        sqls.add(" CREATE TABLE ${name} ( ")
        val lines = columns.map { it.columnSql() }
        sqls.addAll(lines)
        val pc = columns.filter { it.primary }
        if (pc.isEmpty()) {
            val endLine = sqls.takeLast(1)[0].dropLast(1)
            sqls.removeLast()
            sqls.add(endLine)

        } else {
            if (pc.size == 1) {
                val pkSql = " PRIMARY KEY (${pc[0].name})"
                sqls.add(pkSql)
            } else {
                val names = pc.map { it.name }
                val pkEnd = names.joinToString(",")
                val pkSql = " PRIMARY KEY (${pkEnd})"
                sqls.add(pkSql)
            }
        }

        // engine 字符集 等todo
        sqls.add(")")

        if (comment.isNotEmpty()) {
            sqls.add("COMMENT='${comment}'")
        }
        sqls.add(";")
        return sqls.joinToString(DifConst.NEW_LINE)
    }

    fun changeTableScript(to: Table): List<String> {
        val dif = difColumns(to)
        val sqls = mutableListOf<String>()
        val sql = dif!!.updateColumnsSql()
        sqls.add(sql)
        val ncs = dif.newColumnsSql()
        sqls.add(ncs)
        return sqls
    }
}