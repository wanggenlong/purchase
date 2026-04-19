<template>
  <div class="product-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="queryForm.productName"
        placeholder="商品名称"
        clearable
        class="search-input"
        @keyup.enter.native="handleSearch"
      />
      <el-input
        v-model="queryForm.skuCode"
        placeholder="SKU编码"
        clearable
        class="search-input"
        @keyup.enter.native="handleSearch"
      />
      <el-cascader
        v-model="queryForm.categoryNos"
        :options="categoryTree"
        :props="queryCascaderProps"
        placeholder="商品分类"
        clearable
        class="search-cascader"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增商品</el-button>
      <el-button icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="productList" style="width: 100%;" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="skuCode" label="SKU编码" width="140" />
      <el-table-column prop="categoryName" label="分类" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-tag size="small" type="info" v-if="scope.row.categoryName">{{ scope.row.categoryName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="right" />
      <el-table-column prop="purchasePrice" label="采购价" width="100" align="right">
        <template slot-scope="scope">
          ¥{{ scope.row.purchasePrice }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center">
        <template slot-scope="scope">
          <el-button type="text" class="action-link" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" class="action-link delete-link" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 条</span>
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="queryForm.pageNum"
        :page-size="queryForm.pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="productForm" :model="productForm" :rules="productRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU编码" prop="skuCode">
              <el-input v-model="productForm.skuCode" placeholder="请输入SKU编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品分类" prop="categoryNos">
          <el-cascader
            v-model="productForm.categoryNos"
            :options="categoryTree"
            :props="cascaderProps"
            placeholder="请选择分类（需选到第四级）"
            style="width: 100%;"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采购价" prop="purchasePrice">
              <el-input-number
                v-model="productForm.purchasePrice"
                :min="0.01"
                :precision="2"
                :step="1"
                controls-position="right"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProductPage, addProduct, updateProduct, deleteProduct, exportProduct, getCategoryTree } from '../api/purchase';

export default {
  name: 'ProductListView',
  data() {
    return {
      loading: false,
      submitLoading: false,
      productList: [],
      total: 0,
      categoryTree: [],
      cascaderProps: {
        value: 'categoryNo',
        label: 'categoryName',
        children: 'children',
        checkStrictly: false,
        emitPath: true
      },
      queryCascaderProps: {
        value: 'categoryNo',
        label: 'categoryName',
        children: 'children',
        checkStrictly: true,
        emitPath: true
      },
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        productName: '',
        skuCode: '',
        categoryNos: []
      },
      dialogVisible: false,
      isEdit: false,
      productForm: {
        id: null,
        productName: '',
        skuCode: '',
        categoryNos: [],
        categoryNo: '',
        purchasePrice: null,
        description: ''
      },
      productRules: {
        productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
        categoryNos: [{ required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: 4 }],
        purchasePrice: [{ required: true, message: '请输入采购价', trigger: 'blur' }]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑商品' : '新增商品';
    }
  },
  created() {
    this.loadCategoryTree();
    this.loadProducts();
  },
  methods: {
    async loadCategoryTree() {
      const result = await getCategoryTree();
      this.categoryTree = result.data || [];
    },
    async loadProducts() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.queryForm.pageNum,
          pageSize: this.queryForm.pageSize,
          productName: this.queryForm.productName || undefined,
          skuCode: this.queryForm.skuCode || undefined,
          categoryNo: (this.queryForm.categoryNos && this.queryForm.categoryNos.length > 0) ? this.queryForm.categoryNos[this.queryForm.categoryNos.length - 1] : undefined
        };
        const result = await getProductPage(params);
        this.productList = result.data.records;
        this.total = result.data.total;
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.queryForm.pageNum = 1;
      this.loadProducts();
    },
    handleReset() {
      this.queryForm = {
        pageNum: 1,
        pageSize: 10,
        productName: '',
        skuCode: '',
        categoryNos: []
      };
      this.loadProducts();
    },
    handlePageChange(page) {
      this.queryForm.pageNum = page;
      this.loadProducts();
    },
    handleAdd() {
      this.isEdit = false;
      this.productForm = {
        id: null,
        productName: '',
        skuCode: '',
        categoryNos: [],
        categoryNo: '',
        purchasePrice: null,
        description: ''
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.productForm && this.$refs.productForm.clearValidate();
      });
    },
    handleEdit(row) {
      this.isEdit = true;
      this.productForm = {
        id: row.id,
        productName: row.productName,
        skuCode: row.skuCode,
        categoryNos: this.buildCategoryPath(row.categoryNo),
        categoryNo: row.categoryNo,
        purchasePrice: row.purchasePrice,
        description: row.description || ''
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.productForm && this.$refs.productForm.clearValidate();
      });
    },
    buildCategoryPath(categoryNo) {
      const path = [];
      const findPath = (nodes, targetNo, currentPath) => {
        for (const node of nodes) {
          const newPath = [...currentPath, node.categoryNo];
          if (node.categoryNo === targetNo) {
            path.push(...newPath);
            return true;
          }
          if (node.children && node.children.length > 0) {
            if (findPath(node.children, targetNo, newPath)) return true;
          }
        }
        return false;
      };
      findPath(this.categoryTree, categoryNo, []);
      return path;
    },
    handleSubmit() {
      this.$refs.productForm.validate(valid => {
        if (!valid) return;
        this.submitLoading = true;

        const categoryNo = this.productForm.categoryNos[this.productForm.categoryNos.length - 1];
        const data = {
          productName: this.productForm.productName,
          skuCode: this.productForm.skuCode,
          categoryNo: categoryNo,
          purchasePrice: this.productForm.purchasePrice,
          description: this.productForm.description
        };

        const request = this.isEdit
          ? updateProduct({ ...data, id: this.productForm.id })
          : addProduct(data);

        request.then(() => {
          this.$message.success(this.isEdit ? '修改成功' : '新增成功');
          this.dialogVisible = false;
          this.loadProducts();
        }).catch(err => {
          this.$message.error(err.response?.data?.msg || '操作失败');
        }).finally(() => {
          this.submitLoading = false;
        });
      });
    },
    handleDelete(row) {
      this.$confirm('确定删除商品"' + row.productName + '"？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteProduct({ id: row.id }).then(() => {
          this.$message.success('删除成功');
          this.loadProducts();
        }).catch(err => {
          this.$message.error(err.response?.data?.msg || '删除失败');
        });
      }).catch(() => {});
    },
    async handleExport() {
      try {
        const params = {
          productName: this.queryForm.productName || undefined,
          skuCode: this.queryForm.skuCode || undefined,
          categoryNo: (this.queryForm.categoryNos && this.queryForm.categoryNos.length > 0) ? this.queryForm.categoryNos[this.queryForm.categoryNos.length - 1] : undefined
        };
        const result = await exportProduct(params);
        const blob = new Blob([result], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = '商品列表.xlsx';
        link.click();
        window.URL.revokeObjectURL(url);
        this.$message.success('导出成功');
      } catch (err) {
        this.$message.error('导出失败');
      }
    }
  }
};
</script>

<style scoped>
.product-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.search-bar {
  padding: 16px 24px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.search-cascader {
  width: 260px;
}

.action-bar {
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-bar {
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e0e0e0;
}

.total-text {
  color: #5f6368;
  font-size: 13px;
}

.action-link {
  color: #1a73e8 !important;
  font-size: 13px;
  padding: 0;
}

.delete-link {
  color: #d93025 !important;
}

.action-link:hover,
.delete-link:hover {
  opacity: 0.8;
}

.search-cascader >>> .el-cascader-node .el-radio {
  display: none;
}
</style>
