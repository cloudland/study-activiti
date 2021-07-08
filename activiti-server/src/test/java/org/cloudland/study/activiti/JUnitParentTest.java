/**
 * FileName: JUnitParentTest.java
 * Author: Lei
 * Company: Cloudland Studio
 * Createdate: 2018-09-09 16:03
 * <p>
 * All rights Reserved, Designed By cloudland Copyright(C) 2010-2018
 */
package org.cloudland.study.activiti;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <pre>
 * 测试用例继承基础类
 * </pre>
 *
 * @author Lei
 * @version v1.0
 * @ClassName JUnitParentTest
 * @see
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持
@SpringBootTest(classes = {JunitApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JUnitParentTest {

    /**
     * 日志对象
     */
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取日志对象
     */
    protected Logger getLogger() {
        return this.LOGGER;
    }

}

@SpringBootApplication(scanBasePackages = {"org.cloudland.study.activiti"})
@EnableTransactionManagement
class JunitApplication {
    public static void main(String[] args) {
        SpringApplication.run(JunitApplication.class, args);
    }
}