<script setup>
import {Bicycle, Box, List, Plus, Search} from "@element-plus/icons-vue";
</script>
<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
          router
          :default-active="$route.path"
          class="el-menu-vertical"
      >
        <el-menu-item index="/create">
          <el-icon><Plus /></el-icon>
          <span>快速下单 (用户)</span>
        </el-menu-item>

        <el-menu-item index="/search">
          <el-icon><Search /></el-icon>
          <span>轨迹查询 (用户)</span>
        </el-menu-item>

        <el-divider /> <el-menu-item index="/list">
        <el-icon><List /></el-icon>
        <span>全量订单 (管理员)</span>
      </el-menu-item>

        <el-menu-item index="/warehouse">
          <el-icon><Box /></el-icon>
          <span>仓储作业 (仓管)</span>
        </el-menu-item>

        <el-menu-item index="/courier">
          <el-icon><Bicycle /></el-icon>
          <span>末端派送 (快递员)</span>
        </el-menu-item>

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