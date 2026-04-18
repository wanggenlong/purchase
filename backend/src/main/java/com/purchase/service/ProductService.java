package com.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.purchase.dto.ProductCreateDTO;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.dto.ProductUpdateDTO;
import com.purchase.vo.ProductExportVO;
import com.purchase.vo.ProductVO;

import java.util.List;

public interface ProductService {

    /** 分页查询商品 */
    IPage<ProductVO> pageProducts(ProductPageQueryDTO queryDTO);

    /** 新增商品 */
    void addProduct(ProductCreateDTO createDTO);

    /** 修改商品 */
    void updateProduct(ProductUpdateDTO updateDTO);

    /** 删除商品（逻辑删除） */
    void deleteProduct(Long id);

    /** 查询导出数据 */
    List<ProductExportVO> listExportData(ProductPageQueryDTO queryDTO);
}
