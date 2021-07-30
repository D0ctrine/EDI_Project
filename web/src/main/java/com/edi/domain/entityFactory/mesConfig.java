package com.edi.domain.entityFactory;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    basePackages = { "com.edi.infrastructure.SecondRepository" },
    entityManagerFactoryRef = "mesEntityManager",
    transactionManagerRef = "mesTransactionManager"
)
public class mesConfig {
  @Autowired
  Environment env;

  @Bean
  public LocalContainerEntityManagerFactoryBean mesEntityManager() {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(mesDataSource());
      em.setPackagesToScan(new String[] {"com.edi.domain.secondModel"});
      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      em.setJpaVendorAdapter(vendorAdapter);
      HashMap<String, Object> properties = new HashMap<>();
      properties.put("hibernate.ddl-auto", env.getProperty("hibernate.ddl-auto"));
      properties.put("database-platform", env.getProperty("database-platform"));
      properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
      em.setJpaPropertyMap(properties);

      return em;
  }

  @Bean
  @ConfigurationProperties(prefix="spring.mes")
  public DataSource mesDataSource() {
      return DataSourceBuilder.create().build();
  }

  @Bean
  public PlatformTransactionManager mesTransactionManager() {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(mesEntityManager().getObject());
      return transactionManager;
  }
}
