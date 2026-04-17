<template>
  <el-container style="min-height: 100vh;">
    <el-aside width="220px">
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/">系统首页</el-menu-item>
        <el-menu-item index="/products">商品列表</el-menu-item>
        <el-menu-item index="/purchase-orders">采购单列表</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 20px; font-weight: 600;">电商采购管理系统</span>
        <div style="display: flex; align-items: center; gap: 16px;">
          <span class="username">{{ realName || username }}</span>
          <el-button type="text" @click="handleLogout" class="logout-btn">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'AppLayout',
  data() {
    return {
      username: localStorage.getItem('username') || '',
      realName: localStorage.getItem('realName') || ''
    };
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('realName');
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.username {
  color: #5f6368;
  font-size: 14px;
}
.logout-btn {
  color: #1a73e8;
  font-size: 14px;
}
.logout-btn:hover {
  color: #1765cc;
}
</style>
