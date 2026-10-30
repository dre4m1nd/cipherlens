package com.dre4m1nd.cipherlens.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.dto.OperationLogQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.vo.OperationLogVO;
import com.dre4m1nd.cipherlens.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author dre4m1nd
 */
@Tag(name = "操作日志接口")
@RequestMapping("/log")
@RestController
@Slf4j
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @Operation(summary = "查询当前用户所有操作日志接口")
    @PostMapping("/list")
    public Result<IPage<OperationLogVO>> listLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  @RequestBody(required = false) OperationLogQueryDTO operationLogQueryDTO) {
        PageQueryDTO pageQueryDTO = new PageQueryDTO(pageNum, pageSize);
        if (operationLogQueryDTO == null) {
            operationLogQueryDTO = new OperationLogQueryDTO();
        }
        return operationLogService.listLogs(pageQueryDTO, operationLogQueryDTO);
    }

}
