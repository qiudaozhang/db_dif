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
 * @author qiudaozhang
 * Dif列结构定义
 */
data class DifColumn(
    val tableName: String,
    val newColumns: List<Column>,
    val updateColumns: List<Pair<Column, Column>>,
    val deleteColumns: List<Column>,
) {


    /**
     * 全SQL
     */
    fun fullSQL(): String {
        return updateColumnsSql() + DifConst.NEW_LINE + newColumnsSql()
    }


    /**
     * 获取更新列的SQL脚本
     */
    fun updateColumnsSql(): String {
        val columnSqls = mutableListOf<String>()
        updateColumns.forEach { (from, to) ->
            val sqls = mutableListOf<String>()
            sqls.add(" ALTER TABLE ${tableName} CHANGE `${from.name}` `${from.name}` ")
            sqls.add(" ${from.type} ")
            if (from.length > 0) {
                if (from.type == "DECIMAL") {
                    sqls.add(" (${from.length},${from.digital}) ")
                } else {
                    if (from.type != "JSON") {
                        sqls.add(" (${from.length}) ")
                    }
                }
            }
            if (from.nullable != to.nullable) {
                if (from.nullable) {
                    sqls.add(" NOT NULL ")
                } else {
                    sqls.add(" NULL ")
                }
            }
            from.defaultValue?.let {
                sqls.add(" DEFAULT '${from.defaultValue}' ")
            }

            if (from.comment != to.comment) {
                sqls.add(" COMMENT '${from.comment}' ")
            }
            sqls.add(" ; ")
            val cs = sqls.joinToString("")
            columnSqls.add(cs)
        }

        if (columnSqls.isNotEmpty()) {
            return columnSqls.joinToString(DifConst.NEW_LINE)
        }

        return DifConst.COMMENT_SQL
    }


    /**
     * 新列生成SQL语句
     */
    fun newColumnsSql(): String {
        val columnSqls = mutableListOf<String>()
        newColumns.forEach { nc ->
            val sqls = mutableListOf<String>()
            sqls.add(" ALTER TABLE ${tableName} ADD `${nc.name}` ${nc.type} ")
            if (nc.length > 0) {
                if (nc.type == "DECIMAL") {
                    sqls.add(" (${nc.length},${nc.digital}) ")
                } else {
                    if (nc.type != "JSON") {
                        sqls.add(" (${nc.length}) ")
                    }
                }
            }
            if (!nc.nullable) {
                sqls.add(" NOT NULL ")
            }
            nc.defaultValue?.let {
                sqls.add(" DEFAULT '${nc.defaultValue}' ")
            }

            if (nc.comment.isNotEmpty()) {
                sqls.add(" COMMENT '${nc.comment}' ")
            }
            sqls.add(" ; ")
            val cs = sqls.joinToString("")
            columnSqls.add(cs)
        }
        if (columnSqls.isNotEmpty()) {
            return columnSqls.joinToString(DifConst.NEW_LINE)
        }

        return DifConst.COMMENT_SQL
    }

}