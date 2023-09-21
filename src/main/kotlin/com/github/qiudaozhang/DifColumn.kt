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
 * Dif列结构定义
 */
data class DifColumn(
    val tableName:String,
    val newColumns:List<Column>,
    val updateColumns:List<Pair<Column,Column>>,
    val deleteColumns:List<Column>,
)
{


    /**
     * 全SQL
     */
    fun fullSQL():String{
        return updateColumnsSql() + DifConst.NEW_LINE + newColumnsSql()
    }


    /**
     * 获取更新列的SQL脚本
     */
    fun updateColumnsSql():String{
        val columnSqls = mutableListOf<String>()
        updateColumns.forEach {
            (s,e)->
            val sqls = mutableListOf<String>()
            sqls.add(" alter table ${tableName} change ${e.name} ")
            if(e.length > 0){
                sqls.add(" ${e.type} ")
            } else {
                sqls.add(" ${e.type}(${e.length}) ")
            }
            if(s.nullable != e.nullable){
                if(e.nullable){
                    sqls.add(" not null ")
                } else{
                    sqls.add(" null ")
                }
            }
            e.defaultValue?.let{
                sqls.add(" default '${e.defaultValue}' ")
            }

            if(s.comment != e.comment){
                sqls.add(" comment '${e.comment}' ")
            }
            sqls.add(" ; ")
            val cs = sqls.joinToString("")
            columnSqls.add(cs)
        }

        if(columnSqls.isNotEmpty()){
            return columnSqls.joinToString(DifConst.NEW_LINE)
        }

        return DifConst.COMMENT_SQL
    }


    /**
     * 新列生成SQL语句
     */
    fun newColumnsSql():String{
        val columnSqls = mutableListOf<String>()
        newColumns.forEach {
            nc->
            val sqls = mutableListOf<String>()
            sqls.add(" alter table ${tableName} add ${nc.name} ")
            if(nc.length > 0){
                sqls.add(" ${nc.type} ")
            } else {
                sqls.add(" ${nc.type}(${nc.length}) ")
            }
            if(!nc.nullable){
                sqls.add(" not null ")
            }
            nc.defaultValue?.let{
                sqls.add(" default '${nc.defaultValue}' ")
            }

            if(nc.comment.isEmpty()){
             sqls.add(" comment '${nc.comment}' ")
            }
            sqls.add(" ; ")
            val cs = sqls.joinToString("")
            columnSqls.add(cs)
        }
        if(columnSqls.isNotEmpty()){
            return columnSqls.joinToString(DifConst.NEW_LINE)
        }

        return DifConst.COMMENT_SQL
    }

}