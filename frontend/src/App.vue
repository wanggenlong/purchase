<template>
  <div id="app">
    <AppLayout v-if="showLayout" />
    <Login v-else />
  </div>
</template>

<script>
import AppLayout from './components/AppLayout.vue';
import Login from './views/Login.vue';

export default {
  name: 'AppRoot',
  components: { AppLayout, Login },
  computed: {
    showLayout() {
      return this.$route.path !== '/login' && localStorage.getItem('token');
    }
  }
};
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>
