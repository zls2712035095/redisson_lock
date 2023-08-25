package com.zls.main.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class RedissonConf {
    //读取环境变量的实例env
    @Autowired
    private Environment env;

    /** 自定义注入配置操作Redisson的客户端实例
     *
     *
     */
    @Bean
    public RedissonClient config(){
        //创建配置实例
        Config config=new Config();
        //可以设置传输模式为EPOll，也可以设置为NIO等
        //config.setTransportMode(TransportMode.NIO);
        //设置服务节点部署模式：集群模式，单一节点模式，主从模式，哨兵模式等
        //config.useClusterServers().addNodeAddress(env.getProperty("redisson.host.config"),env.getProperty("redisson.host.config"));
        config.useSingleServer().setAddress(env.getProperty("redisson.host.config")).setKeepAlive(true);
        //创建并返回操作Redisson的客户端实例
        return Redisson.create(config);

    }
}
