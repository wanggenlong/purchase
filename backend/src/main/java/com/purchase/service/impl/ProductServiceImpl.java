package com.purchase.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.purchase.common.exception.BusinessException;
import com.purchase.dto.ProductCreateDTO;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.dto.ProductUpdateDTO;
import com.purchase.entity.Category;
import com.purchase.entity.Product;
import com.purchase.mapper.ProductMapper;
import com.purchase.service.CategoryService;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductExportVO;
import com.purchase.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private CategoryService categoryService;

    @Override
    public IPage<ProductVO> pageProducts(ProductPageQueryDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        var query = lambdaQuery()
                .eq(Product::getIsDelete, 0)
                .like(StringUtils.isNotBlank(queryDTO.getProductName()), Product::getProductName, queryDTO.getProductName())
                .like(StringUtils.isNotBlank(queryDTO.getSkuCode()), Product::getSkuCode, queryDTO.getSkuCode());
        if (queryDTO.getCategoryId() != null) {
            query.in(Product::getCategoryId, categoryService.findDescendantIds(queryDTO.getCategoryId()));
        }
        IPage<Product> productPage = query.orderByDesc(Product::getUpdateTime).page(page);
        return productPage.convert(this::toProductVO);
    }

    @Override
    public void addProduct(ProductCreateDTO createDTO) {
        validateSkuCodeUnique(createDTO.getSkuCode(), null);
        validateCategoryLeaf(createDTO.getCategoryId());

        Product product = new Product();
        product.setProductName(createDTO.getProductName());
        product.setSkuCode(createDTO.getSkuCode());
        product.setCategoryId(createDTO.getCategoryId());
        product.setStock(0);
        product.setPurchasePrice(createDTO.getPurchasePrice());
        product.setDescription(createDTO.getDescription());
        save(product);
    }

    @Override
    public void updateProduct(ProductUpdateDTO updateDTO) {
        Product existing = lambdaQuery()
                .eq(Product::getId, updateDTO.getId())
                .eq(Product::getIsDelete, 0)
                .one();
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        validateSkuCodeUnique(updateDTO.getSkuCode(), updateDTO.getId());
        validateCategoryLeaf(updateDTO.getCategoryId());

        existing.setProductName(updateDTO.getProductName());
        existing.setSkuCode(updateDTO.getSkuCode());
        existing.setCategoryId(updateDTO.getCategoryId());
        existing.setPurchasePrice(updateDTO.getPurchasePrice());
        existing.setDescription(updateDTO.getDescription());
        updateById(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null || product.getIsDelete() == 1) {
            throw new BusinessException("商品不存在");
        }
        removeById(id);
    }

    @Override
    public List<ProductExportVO> listExportData(ProductPageQueryDTO queryDTO) {
        var query = lambdaQuery()
                .eq(Product::getIsDelete, 0)
                .like(StringUtils.isNotBlank(queryDTO.getProductName()), Product::getProductName, queryDTO.getProductName())
                .like(StringUtils.isNotBlank(queryDTO.getSkuCode()), Product::getSkuCode, queryDTO.getSkuCode());
        if (queryDTO.getCategoryId() != null) {
            query.in(Product::getCategoryId, categoryService.findDescendantIds(queryDTO.getCategoryId()));
        }
        List<Product> products = query.orderByDesc(Product::getUpdateTime).list();

        return products.stream()
                .map(this::toExportVO)
                .collect(Collectors.toList());
    }

    /** 校验SKU编码唯一性 */
    private void validateSkuCodeUnique(String skuCode, Long excludeId) {
        Product existing = lambdaQuery()
                .eq(Product::getSkuCode, skuCode)
                .eq(Product::getIsDelete, 0)
                .ne(excludeId != null, Product::getId, excludeId)
                .one();
        if (existing != null) {
            throw new BusinessException("SKU编码已存在");
        }
    }

    /** 校验分类必须为末级（第4级） */
    private void validateCategoryLeaf(Long categoryId) {
        Category category = categoryService.getActiveCategoryById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getLevel() != 4) {
            throw new BusinessException("商品分类必须选择最末级（第四级）");
        }
    }

    /** 构建分类完整路径名称 */
    private String buildCategoryPath(Long categoryId) {
        if (categoryId == null) {
            return "";
        }
        List<String> pathNames = new ArrayList<>();
        Long currentId = categoryId;
        for (int i = 0; i < 4 && currentId != null && currentId > 0; i++) {
            Category category = categoryService.getCategoryById(currentId);
            if (category == null) {
                break;
            }
            pathNames.addFirst(category.getCategoryName());
            currentId = category.getParentId();
        }
        return String.join("/", pathNames);
    }

    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setProductName(product.getProductName());
        vo.setSkuCode(product.getSkuCode());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(buildCategoryPath(product.getCategoryId()));
        vo.setStock(product.getStock());
        vo.setPurchasePrice(product.getPurchasePrice());
        vo.setDescription(product.getDescription());
        return vo;
    }

    private ProductExportVO toExportVO(Product product) {
        ProductExportVO vo = new ProductExportVO();
        vo.setId(product.getId());
        vo.setProductName(product.getProductName());
        vo.setSkuCode(product.getSkuCode());
        vo.setCategoryName(buildCategoryPath(product.getCategoryId()));
        vo.setStock(product.getStock());
        vo.setPurchasePrice(product.getPurchasePrice());
        vo.setDescription(product.getDescription());
        return vo;
    }
}
