<template>
 <el-row>
   <el-col :span="12">
     <el-form :model="fromConfig" label-width="120px">
       <el-form-item label="【源】用户名">
         <el-input v-model="fromConfig.username" />
       </el-form-item>
       <el-form-item label="密码">
         <el-input v-model="fromConfig.password" type="password"/>
       </el-form-item>
       <el-form-item label="连接地址">
         <el-input v-model="fromConfig.url" />
       </el-form-item>
       <el-form-item label="数据库名称">
         <el-input v-model="fromConfig.dbName" />
       </el-form-item>
       <el-form-item>
         <el-button type="primary" @click="checkFromConnect">检查连接</el-button>
       </el-form-item>
     </el-form>
   </el-col>

   <el-col :span="12">
     <el-form :model="toConfig" label-width="120px">
       <el-form-item label="【目标】用户名">
         <el-input v-model="toConfig.username" />
       </el-form-item>
       <el-form-item label="密码">
         <el-input v-model="toConfig.password" type="password"/>
       </el-form-item>
       <el-form-item label="连接地址">
         <el-input v-model="toConfig.url" />
       </el-form-item>
       <el-form-item label="数据库名称">
         <el-input v-model="toConfig.dbName" />
       </el-form-item>
       <el-form-item>
         <el-button type="primary" @click="checkToConnect">连接检查</el-button>
         <el-button type="primary" @click="onSubmit">执行</el-button>
       </el-form-item>
     </el-form>
   </el-col>
 </el-row>
</template>

<script lang="ts" setup>
import { reactive ,ref } from 'vue'
import axios from "axios";
import { ElMessage } from 'element-plus'

interface DbConfig{
  username?:string,
  password?:string,
  url?:string,
  dbName?:string,
  driverName:string,
}
// do not use same name with ref
const fromConfig:DbConfig = reactive({
  username: '',
  password: '',
  url: '',
  dbName: '',
  driverName:'com.mysql.cj.jdbc.Driver',
})

const fromOk = ref(false)
const toOk = ref(false)

const toConfig:DbConfig= reactive({
  username: '',
  password: '',
  url: '',
  dbName: '',
  driverName:'com.mysql.cj.jdbc.Driver',
})


const onSubmit = () => {
  checkFromConnect()
  if(!fromOk.value){
    return
  }
  checkToConnect()
  if(!toOk.value){
    return
  }
}

const checkFromConnect = () =>{
  axios.post('http://localhost:8080/check',
      fromConfig,
      {
        headers:{
          'Content-Type':'application/json'
        }
      }
  ).then(res=>{
    fromOk.value = res.data
    if(!fromOk.value){
      ElMessage(
          {
            message:'源数据库配置检查未通过',
            type: 'error'
          }
      )
    }
    // console.log('from 检查结果',fromOk.value)
  })
}

const checkToConnect = () =>{
  axios.post('http://localhost:8080/check',
      toConfig,
      {
        headers:{
          'Content-Type':'application/json'
        }
      }
  ).then(res=>{
    toOk.value = res.data
    if(!toOk.value){
      ElMessage(
          {
            message:'目标数据库配置检查未通过',
            type: 'error'
          }
      )
    }

  })
}
</script>
