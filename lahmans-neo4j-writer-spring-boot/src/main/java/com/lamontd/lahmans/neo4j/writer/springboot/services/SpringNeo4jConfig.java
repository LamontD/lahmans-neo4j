/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lamontd.lahmans.neo4j.writer.springboot.services;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.lamontd.lahmans.neo4j.core.LahmansCore;

/**
 *
 * @author lamontdozierjr
 */
@Configuration
@EnableNeo4jRepositories(basePackages = LahmansCore.Consts.MODEL_BASE_PACKAGE)
@EnableTransactionManagement
public class SpringNeo4jConfig {
    
    @Value("${spring.data.neo4j.uri}")
    private String neo4jUri;
    
    @Value("${spring.data.neo4j.username}")
    private String neo4jUsername;
    
    @Value("${spring.data.neo4j.password}")
    private String neo4jPassword;

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(configuration(), LahmansCore.Consts.MODEL_BASE_PACKAGE);
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        ConfigurationSource properties = new ClasspathConfigurationSource("application.properties");
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .uri(neo4jUri)
                .credentials(neo4jUsername, neo4jPassword)
                .build();
        return configuration;
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}
