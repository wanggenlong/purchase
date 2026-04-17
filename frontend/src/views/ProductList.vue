<template>
  <el-card>
    <div slot="header">商品列表</div>
    <el-table :data="products" style="width: 100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="skuCode" label="SKU" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="purchasePrice" label="采购价" />
    </el-table>
  </el-card>
</template>

<script>
import { getProducts } from '../api/purchase';

export default {
  name: 'ProductListView',
  data() {
    return {
      products: []
    };
  },
  created() {
    this.loadProducts();
  },
  methods: {
    async loadProducts() {
      const result = await getProducts();
      this.products = result.data;
    }
  }
};
</script>
