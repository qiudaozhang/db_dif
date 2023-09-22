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
 * 2023-9-22
 * 列定义
 */
data class Column(
    val name: String, // 列的名称
    val type: String, // 列的类型
    val length: Int = 0, // 长度，假设有的话
    val comment: String = "", // 注释
    val nullable: Boolean = true,// 是否允许空
    val defaultValue: Any? = null,
    val primary: Boolean = false, // 是否是主键的一部分
    var sort: Int = 1, // 列的序号 预留
    val autoIncrement: Boolean = false, //  是否自增
    val digital: Int = 0, // 如果是decimal，可能有
) {


    // 比较两个是否是相同的
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Column) {
            return false
        }
        return (name == other.name) && (type == other.type)
                && (comment == other.comment)
                && (primary == other.primary)
                && (length == other.length)
                && (digital == other.digital)
                && (nullable == other.nullable)
    }


    fun columnSql(): String {
        val worlds = mutableListOf<String>()
        worlds.add(" `${name}` ")
        worlds.add(" ${type} ")
        if (length > 0) {
            if (type == "DECIMAL") {
                worlds.add("(${length},${digital})")
            } else {

                if (type != "JSON") {
                    worlds.add("(${length})")
                }
            }

        }
        if (nullable) {
            worlds.add(" null ")
        } else {
            worlds.add(" not null ")
        }
        if (comment.isNotEmpty()) {
            worlds.add(" comment '${comment}' ")
        }
        worlds.add(",")
        return worlds.joinToString("")
    }
}


