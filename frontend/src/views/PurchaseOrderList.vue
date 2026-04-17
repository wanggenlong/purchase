<template>
  <el-card>
    <div slot="header">采购单列表</div>
    <el-table :data="orders" style="width: 100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="采购单号" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column prop="totalAmount" label="总金额" />
      <el-table-column prop="orderStatus" label="状态" />
    </el-table>
  </el-card>
</template>

<script>
import { getPurchaseOrders } from '../api/purchase';

export default {
  name: 'PurchaseOrderListView',
  data() {
    return {
      orders: []
    };
  },
  created() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      const result = await getPurchaseOrders();
      this.orders = result.data;
    }
  }
};
</script>
