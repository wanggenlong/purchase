<template>
  <el-card>
    <div slot="header">系统首页</div>
    <p>这是电商采购管理系统的初始化演示页面。</p>
    <el-button type="primary" @click="loadHealth">检测后端状态</el-button>
    <el-descriptions v-if="health.status" :column="1" style="margin-top: 16px;">
      <el-descriptions-item label="状态">{{ health.status }}</el-descriptions-item>
      <el-descriptions-item label="模式">{{ health.dataMode }}</el-descriptions-item>
      <el-descriptions-item label="应用">{{ health.application }}</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<script>
import { getHealthStatus } from '../api/purchase';

export default {
  name: 'DashboardView',
  data() {
    return {
      health: {}
    };
  },
  created() {
    this.loadHealth();
  },
  methods: {
    async loadHealth() {
      const result = await getHealthStatus();
      this.health = result.data;
    }
  }
};
</script>
