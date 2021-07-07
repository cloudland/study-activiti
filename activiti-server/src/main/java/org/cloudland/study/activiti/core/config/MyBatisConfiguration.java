/**
 * FileName: MyBatisMapperScanConfiguration.java
 * Author: Lei
 * Company: Cloudland Studio
 * Createdate: 2018-09-09 22:16
 * <p>
 * All rights Reserved, Designed By cloudland Copyright(C) 2010-2018
 */
package org.cloudland.study.activiti.core.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <pre>
 * MyBatis Mapper 自动扫描配置
 * </pre>
 *
 * @author Lei
 * @version v1.0
 * @ClassName MyBatisMapperScanConfiguration
 * @see
 */
@Configuration
@ConditionalOnResource(resources = "classpath*:mybatis/*.properties")
@ConditionalOnClass({org.mybatis.spring.boot.autoconfigure.SpringBootVFS.class, org.mybatis.spring.SqlSessionFactoryBean.class, org.mybatis.spring.mapper.MapperScannerConfigurer.class})
public class MyBatisConfiguration {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisConfiguration.class);

    /**
     * MyBatis 配置信息
     */
    private final Configuration configuration = getConfiguration();

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        try {
            // 解决 MyBatis 工程打成 Jar 无法加载配置类问题
            VFS.addImplClass(SpringBootVFS.class);

            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource);
            // 设置别名包（实体类）
            sqlSessionFactoryBean.setTypeAliasesPackage(configuration.getTypeAliasesPackage());
            // 设置 Mapper XML 配置文件路径
            sqlSessionFactoryBean.setMapperLocations(configuration.getMapperLocationArray());

            // 获取 SessionFactory
            return sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            LOGGER.debug("[MyBatis][配置信息]构建 MyBatis SqlSessionFactory 异常", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(configuration.getBasePackage());
        return mapperScannerConfigurer;
    }

    /**
     * 获取包路径内所有 MyBatis 配置信息
     *
     * @return
     */
    private Configuration getConfiguration() {
        // 获取别名包路径
        String basePackage = null, typeAliasPackage = null;
        // Mapper XML 配置文件资源
        Resource[] mapperLocationResourceArray = new Resource[0];

        try {
            // 基础包路径
            List<String> basePackageArray = new ArrayList<>();
            // 配置 XML 文件路径
            List<String> mapperLocationArray = new ArrayList<>();
            // 设置别名包（实体类）
            List<String> typeAliasPackageArray = new ArrayList<>();

            // 查找全部类路径的 MyBatis 配置文件
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/*.properties");
            // 循环获取引用领域 MyBatis 配置信息
            Properties filePro;
            for (Resource resource : resources) {
                filePro = new Properties();
                filePro.load(resource.getInputStream());

                // 追加基础包路径
                basePackageArray.add(filePro.getProperty("mybatis.base-package"));
                // 追加配置 XML 文件路径
                mapperLocationArray.add(filePro.getProperty("mybatis.mapper-locations"));
                // 追加设置别名包（实体类）
                typeAliasPackageArray.add(filePro.getProperty("mybatis.type-aliases-package"));

                LOGGER.debug("[MyBatis][配置信息]获取资源路径[{}]MyBatis配置信息[{}].", resource.getURL(), filePro);
            }

            // 基础包路径
            basePackage = basePackageArray.stream().collect(Collectors.joining(","));

            // Mapper XML 配置文件资源
            Object[] resourceArray = new Object[0];
            for (String location : mapperLocationArray) {
                resourceArray = ArrayUtils.add(resourceArray, new PathMatchingResourcePatternResolver().getResources(location));
            }
            mapperLocationResourceArray = Arrays.stream(resourceArray).flatMap(array -> Arrays.stream((Resource[]) array)).toArray(Resource[]::new);

            // 别名包路径
            typeAliasPackage = typeAliasPackageArray.stream().collect(Collectors.joining(","));

        } catch (Exception e) {
            LOGGER.debug("[MyBatis][配置信息]获取配置资源路径异常.", e);
            throw new RuntimeException(e);
        } finally {
            LOGGER.debug("[MyBatis][配置信息]配置 BasePackage: {}", basePackage, Arrays.stream(mapperLocationResourceArray).map(resource -> resource.getFilename()).collect(Collectors.joining("\n")));
            LOGGER.debug("[MyBatis][配置信息]配置 TypeAliasesPackage: {}", typeAliasPackage);
            LOGGER.debug("[MyBatis][配置信息]配置 MapperLocations: {}", Arrays.stream(mapperLocationResourceArray).map(resource -> resource.getFilename()).collect(Collectors.joining(",")));
        }

        return new Configuration(basePackage, mapperLocationResourceArray, typeAliasPackage);
    }


    @Getter
    @AllArgsConstructor
    private class Configuration {

        /**
         * 基础包路径
         */
        private String basePackage;

        /**
         * 配置 XML 文件路径
         */
        private Resource[] mapperLocationArray;

        /**
         * 设置别名包（实体类）
         */
        private String typeAliasesPackage;

    }

}