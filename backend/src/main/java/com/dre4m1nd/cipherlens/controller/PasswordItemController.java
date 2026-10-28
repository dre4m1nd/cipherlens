package com.dre4m1nd.cipherlens.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemQueryDTO;
import com.dre4m1nd.cipherlens.model.vo.PasswordItemVO;
import com.dre4m1nd.cipherlens.service.PasswordItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dre4m1nd
 */
@Tag(name = "密码项接口")
@RequestMapping("/passwordItem")
@RestController
@Slf4j
public class PasswordItemController {

    @Resource
    private PasswordItemService passwordItemService;

    @Operation(summary = "添加密码项接口")
    @PostMapping("/add")
    public Result<?> addPasswordItem(@Validated @RequestBody PasswordItemDTO passwordItemDTO) throws Exception {
        return passwordItemService.addPasswordItem(passwordItemDTO);
    }

    @Operation(summary = "根据密码Id删除密码项接口")
    @DeleteMapping("/remove/{id}")
    public Result<?> removePasswordItemById(@PathVariable Long id) {
        return passwordItemService.removePasswordItem(id);
    }

    @Operation(summary = "根据密码Id更新密码项接口")
    @PostMapping("/update/{id}")
    public Result<?> updatePasswordItemById(@PathVariable Long id, @Validated @RequestBody PasswordItemDTO passwordItemDTO) {
        log.info("更新密码:{}",id);
        return passwordItemService.updatePasswordItem(id,passwordItemDTO);
    }

    @Operation(summary = "根据密码Id查询密码项接口")
    @GetMapping("/query/{id}")
    public Result<PasswordItemVO> queryPasswordItemById(@PathVariable Long id) {
        log.info("查询密码:{}",id);
        return passwordItemService.queryPasswordItemById(id);
    }

    @Operation(summary = "查询所有密码项接口")
    @PostMapping("/list")
    public Result<IPage<PasswordItemVO>> listPasswordItem(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestBody PasswordItemQueryDTO passwordItemQueryDTO
            ) {
        PageQueryDTO pageQueryDTO = new PageQueryDTO(pageNum, pageSize);
        if (passwordItemQueryDTO == null) {
            passwordItemQueryDTO = new PasswordItemQueryDTO();
        }
        return passwordItemService.listPasswordItem(pageQueryDTO, passwordItemQueryDTO);
    }

    @Operation(summary = "获取明文密码")
    @GetMapping("/getPlainText/{id}")
    public Result<PasswordItemVO> getPlainText(@PathVariable Long id) throws Exception {
        return passwordItemService.getPlainText(id);
    }
}
