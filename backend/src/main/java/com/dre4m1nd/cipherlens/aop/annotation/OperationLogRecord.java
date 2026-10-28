package com.dre4m1nd.cipherlens.aop.annotation;


import com.dre4m1nd.cipherlens.common.Enum.OperationContentEnum;
import com.dre4m1nd.cipherlens.common.Enum.OperationTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author dre4m1nd
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogRecord {

    OperationTypeEnum operationType();

    OperationContentEnum operationContent();

}
