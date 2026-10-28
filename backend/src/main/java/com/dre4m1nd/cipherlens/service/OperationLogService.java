package com.dre4m1nd.cipherlens.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.dto.OperationLogQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.entity.OperationLog;
import com.dre4m1nd.cipherlens.model.vo.OperationLogVO;

import java.util.List;

/**
* @author dre4m1nd
* @description 针对表【operation_log(操作审计日志表)】的数据库操作Service
* @createDate 2025-12-05 10:59:29
*/
public interface OperationLogService extends IService<OperationLog> {

    Result<IPage<OperationLogVO>> listLogs(PageQueryDTO pageQueryDTO, OperationLogQueryDTO operationLogQueryDTO);
}
