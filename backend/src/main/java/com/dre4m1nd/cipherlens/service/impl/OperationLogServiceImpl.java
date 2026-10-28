package com.dre4m1nd.cipherlens.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.common.util.UserHolderUtil;
import com.dre4m1nd.cipherlens.mapper.OperationLogMapper;
import com.dre4m1nd.cipherlens.model.dto.OperationLogQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.entity.OperationLog;
import com.dre4m1nd.cipherlens.model.vo.OperationLogVO;
import com.dre4m1nd.cipherlens.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 操作日志Service实现类
 * @author dre4m1nd
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog>
        implements OperationLogService {

    /**
     * 分页查询当前用户操作日志（多条件筛选）
     * @param pageQueryDTO 分页参数
     * @param operationLogQueryDTO 查询条件
     * @return 分页结果VO
     */
    @Override
    public Result<IPage<OperationLogVO>> listLogs(PageQueryDTO pageQueryDTO, OperationLogQueryDTO operationLogQueryDTO) {
        // 构建分页对象
        Page<OperationLog> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<OperationLog> queryWrapper = new LambdaQueryWrapper<>();
        // 仅查当前用户日志
        queryWrapper.eq(OperationLog::getUserId, UserHolderUtil.get());
        // 操作类型筛选（非null才拼接）
        queryWrapper.eq(StrUtil.isNotBlank(operationLogQueryDTO.getOperationType()),
                OperationLog::getOperationType, operationLogQueryDTO.getOperationType());
        // 操作内容模糊查询（非空才拼接）
        queryWrapper.like(StrUtil.isNotBlank(operationLogQueryDTO.getOperationContent()),
                OperationLog::getOperationContent, operationLogQueryDTO.getOperationContent());
        // 时间范围筛选（非null才拼接）
        queryWrapper.ge(operationLogQueryDTO.getStartTime() != null,
                OperationLog::getOperationTime, operationLogQueryDTO.getStartTime());
        queryWrapper.le(operationLogQueryDTO.getEndTime() != null,
                OperationLog::getOperationTime, operationLogQueryDTO.getEndTime());

        // 执行分页查询
        IPage<OperationLog> logPage = this.page(page, queryWrapper);

        // Entity转VO并返回
        return Result.success(logPage.convert(log -> BeanUtil.copyProperties(log, OperationLogVO.class)));
    }
}