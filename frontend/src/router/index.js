import Vue from 'vue';
import Router from 'vue-router';
import Dashboard from '../views/Dashboard.vue';
import ProductList from '../views/ProductList.vue';
import PurchaseOrderList from '../views/PurchaseOrderList.vue';
import Login from '../views/Login.vue';

Vue.use(Router);

const routes = [
  { path: '/login', name: 'login', component: Login },
  { path: '/', name: 'dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/products', name: 'products', component: ProductList, meta: { requiresAuth: true } },
  { path: '/purchase-orders', name: 'purchaseOrders', component: PurchaseOrderList, meta: { requiresAuth: true } }
];

const createRouter = () => new Router({
  mode: 'history',
  routes
});

const router = createRouter();

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.path === '/login' && token) {
    next('/');
  } else if (to.path === '/' && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
