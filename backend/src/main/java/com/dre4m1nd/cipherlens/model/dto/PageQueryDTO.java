package com.dre4m1nd.cipherlens.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author dre4m1nd
 */
@Data
public class PageQueryDTO {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页条数")
    private Integer pageSize;

    /**
     * 构造时做兜底校验：Controller 直接 new 出来的对象不会走 Bean Validation，
     * 这里统一收敛非法值，避免 pageSize 被无限放大导致慢查询。
     */
    public PageQueryDTO(Integer pageNum, Integer pageSize) {
        this.pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        if (pageSize == null || pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else {
            this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        }
    }
}
