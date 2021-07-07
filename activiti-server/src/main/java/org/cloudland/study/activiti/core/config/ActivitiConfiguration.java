/**
 * FileName: ActivitiConfiguration.java
 * Author: Lei
 * Company: Cloudland Studio
 * Createdate: 2021-06-19 12:16
 * <p>
 * All rights Reserved, Designed By cloudland Copyright(C) 2010-2021
 */
package org.cloudland.study.activiti.core.config;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

/**
 * Activiti 配置类
 *
 * @author Lei
 * @version v1.0
 * @ClassName AlibabaDruidConfiguration
 * @see
 */
//@Configuration
//@PropertySource(value = "classpath:druid/druid.properties")
public class ActivitiConfiguration {

    @Bean
    public ProcessEngineConfiguration build(DataSource dataSource, TransactionManager transactionManager) {

        SpringProcessEngineConfiguration engineConfiguration = new SpringProcessEngineConfiguration();

        engineConfiguration.setDataSource(dataSource);
        engineConfiguration.setTransactionManager((PlatformTransactionManager)transactionManager);
        engineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        return engineConfiguration;
    }

}
