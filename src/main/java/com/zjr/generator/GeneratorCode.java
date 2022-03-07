package com.zjr.generator;


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Scanner;

/**
 *
 */
public class GeneratorCode {
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java/");
        // 作者
        gc.setAuthor("zjr");
        // 打开输出目录
        gc.setOpen(false);
        // xml开启BaseResultMap
        gc.setBaseResultMap(true);
        // xml开启BaseColumnList
        gc.setBaseColumnList(true);
        // 日期格式，采用Date
        gc.setDateType(DateType.ONLY_DATE);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/seckill?useSSL=false&characterEncoding=utf-8&serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("300074");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.zjr.seckill")
                .setEntity("entity")
                .setMapper("mapper")
                .setService("service")
                .setServiceImpl("service.impl")
                .setController("controller");
        mpg.setPackageInfo(pc);

        // 配置自定义输出模板
        TemplateConfig templateConfig = new TemplateConfig();
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
//         templateConfig.setEntity("templates/generator/entity.java");
//         templateConfig.setService();
//         templateConfig.setController();
//        templateConfig.setXml(null);
//        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 数据库表映射到实体的命名策略   (下划线 转 驼峰)
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 数据库表字段映射到实体的命名策略   (下划线 转 驼峰)
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // lombok模型
        strategy.setEntityLombokModel(true);
        // 生成@RestController
        strategy.setInclude(scanner("表名, 多个英文逗号分割"));
        strategy.setControllerMappingHyphenStyle(true);
        // 表前缀
        strategy.setTablePrefix("t_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
