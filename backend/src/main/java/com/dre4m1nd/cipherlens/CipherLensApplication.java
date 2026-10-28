package com.dre4m1nd.cipherlens;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.dre4m1nd.cipherlens.mapper")
public class CipherLensApplication {

	public static void main(String[] args) {
		SpringApplication.run(CipherLensApplication.class, args);
	}

}
