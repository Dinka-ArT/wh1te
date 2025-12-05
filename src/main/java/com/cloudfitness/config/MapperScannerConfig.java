package com.cloudfitness.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(value = "com.cloudfitness.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class MapperScannerConfig {
    // 单独的配置类用于 Mapper 扫描，避免与 MyBatis-Plus 配置冲突
}


