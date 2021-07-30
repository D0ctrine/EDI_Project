package com.edi.domain.entityFactory;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableTransactionManagement
@EnableJpaRepositories(
  basePackages = { "com.edi.infrastructure.repository" } ,
  entityManagerFactoryRef = "masterEntityManager",
  transactionManagerRef = "masterTransactionManager"
)
public class mainConfig {
  @Autowired
  Environment env;

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean masterEntityManager(){
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(matserDataSource());
      em.setPackagesToScan(new String[] {"com.edi.domain.model"});
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      em.setJpaVendorAdapter(vendorAdapter);
      HashMap<String, Object> properties = new HashMap<>();
      properties.put("hibernate.ddl-auto", env.getProperty("hibernate.ddl-auto"));
      properties.put("database-platform", env.getProperty("database-platform"));
      em.setJpaPropertyMap(properties);

      return em;
  }

  @Bean
  @ConfigurationProperties(prefix="spring.datasource")
  public DataSource matserDataSource() {
      return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean
  public PlatformTransactionManager masterTransactionManager() {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(masterEntityManager().getObject());
      return transactionManager;
  }

}
