<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu :default-active="$route.path" router class="el-menu-vertical">
        <el-menu-item index="/create">📦 快速下单</el-menu-item>
        <el-menu-item index="/list">📋 订单管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main>
        <router-view /> </el-main>
    </el-container>
  </el-container>
</template>

<style>
.layout-container { height: 100vh; }
.el-aside { background-color: #304156; color: white; }
.el-menu-vertical { border-right: none; height: 100%; }
</style>