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
package com.github.qiudaozhang.controller

import com.github.qiudaozhang.model.DbParser
import com.github.qiudaozhang.request.ConnectRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * db控制器
 *
 */
@RestController
class DbController {

    @PostMapping(value = ["check"])
    fun checkConnection(@RequestBody param: ConnectRequest): Boolean {
        val dp = DbParser(param.username, param.password, param.driverName, param.url, param.dbName)
        return dp.ok()
    }
}