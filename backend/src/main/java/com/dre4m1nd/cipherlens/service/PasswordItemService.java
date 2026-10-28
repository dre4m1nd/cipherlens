package com.dre4m1nd.cipherlens.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dre4m1nd.cipherlens.common.model.Result;
import com.dre4m1nd.cipherlens.model.dto.PageQueryDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemDTO;
import com.dre4m1nd.cipherlens.model.dto.PasswordItemQueryDTO;
import com.dre4m1nd.cipherlens.model.entity.PasswordItem;
import com.dre4m1nd.cipherlens.model.vo.PasswordItemVO;

import java.util.List;

/**
* @author dre4m1nd
* @description 针对表【password_item(密码条目表)】的数据库操作Service
* @createDate 2025-12-05 10:59:11
*/
public interface PasswordItemService extends IService<PasswordItem> {

    Result<?> addPasswordItem(PasswordItemDTO passwordItemDTO) throws Exception;

    Result<?> removePasswordItem(Long id);

    Result<?> updatePasswordItem(Long id, PasswordItemDTO passwordItemDTO);

    Result<PasswordItemVO> queryPasswordItemById(Long id);

    Result<IPage<PasswordItemVO>> listPasswordItem(PageQueryDTO pageQueryDTO, PasswordItemQueryDTO passwordItemQueryDTO);

    Result<PasswordItemVO> getPlainText(Long id) throws Exception;

}
