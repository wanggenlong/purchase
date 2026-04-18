package com.purchase.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDeleteDTO {

    /** 商品ID */
    @NotNull(message = "商品ID不能为空")
    private Long id;
}
