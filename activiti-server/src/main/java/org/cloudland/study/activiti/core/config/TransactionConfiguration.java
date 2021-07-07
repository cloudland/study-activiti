/**
 * FileName: TransactionConfiguration.java
 * Author: Lei
 * Company: Cloudland Studio
 * Createdate: 2020-06-29 22:10
 * <p>
 * All rights Reserved, Designed By cloudland Copyright(C) 2010-2020
 */
package org.cloudland.study.activiti.core.config;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 事物控制配置
 * </pre>
 *
 * @author Lei
 * @version v1.0
 * @ClassName TransactionConfiguration
 * @see
 */
@Configuration
public class TransactionConfiguration {

    /**
     * 构建事物管理器
     *
     * @param dataSource 数据源
     * @return
     */
    @Bean("txManager")
    public TransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 事务管理配置
     *
     * @param txManager 构建事物管理器
     * @return
     */
    @Bean("txAdvice")
    public TransactionInterceptor txAdvice(@Qualifier("txManager") TransactionManager txManager) {
        // 设置只读事务管理的模式（适用于“查”）
        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        // 设置事务是否“只读”(非必需，只是声明该事务中不会进行修改数据库的操作，可减轻由事务造成的数据库压力，属于性能优化的推荐配置)
        readOnly.setReadOnly(true);
        // 设置隔离级别(存在事务则挂起该事务，执行当前逻辑，结束后再恢复上下文事务)
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        // 当抛出设置的对应异常后，进行事务回滚(此处设置为“Exception”级别)
//        readOnly.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        // 设置传播行为(读已提交的数据)
//        readOnly.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);


        // 设置更新事务管理的模式(适用于“增删改”)
        RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
        // 当抛出设置的对应异常后，进行事务回滚(此处设置为“Exception”级别)
        required.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        // 设置隔离级别(存在事务则加入其中，不存在则新建事务)
        required.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 设置传播行为(读已提交的数据)
//        required.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置超时时间
//        required.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);


        // 事务管理规则，承载需要进行事务管理的方法名(模糊匹配)及设置的事务管理属性
        NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();

        // 建立一个map，用来储存要需要进行事务管理的方法名(模糊匹配)
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        // 只读事物
        txMap.put("find*", readOnly);
        txMap.put("query*", readOnly);
        // 更新事物
        txMap.put("*", required);

        // 注入设置好的map
        txAttributeSource.setNameMap(txMap);

        // 实例化事务拦截器
        return new TransactionInterceptor(txManager, txAttributeSource);
    }

    /**
     * 配置事物拦截规则
     * <p>
     * 利用 AspectJExpressionPointcut 设置切面
     *
     * @param txAdvice 事务管理配置
     * @return
     */
    @Bean("txPointcut")
    public DefaultPointcutAdvisor txAdviceAdvisor(@Qualifier("txAdvice") TransactionInterceptor txAdvice) {
        // 声明切点要切入的面
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 设置需要被拦截的路径
        pointcut.setExpression("execution(* org.cloudland.study.activiti.server.service..*.*(..))");
        // 设置切面和配置好的事务管理
        return new DefaultPointcutAdvisor(pointcut, txAdvice);
    }

}