package org.cloudland.study.activiti.init;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.cloudland.study.activiti.JUnitParentTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Map;

public class ActivitiDataBasesInitTest extends JUnitParentTest {

    @Resource
    ProcessEngineConfiguration configuration;

    @Test
    public void init() {
//        Map<String, ProcessEngine> processEngineMapping = ProcessEngines.getProcessEngines();
//        processEngineMapping.keySet().stream().forEach(System.out::println);

        ProcessEngine processEngine = configuration.buildProcessEngine();


        getLogger().info("{}", processEngine);

    }

}
