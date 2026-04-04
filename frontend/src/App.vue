<script setup>
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import MainView from '@/views/MainView.vue'
</script>

<template>
  <el-config-provider :locale="zhCn">
    <MainView />
  </el-config-provider>
</template>
