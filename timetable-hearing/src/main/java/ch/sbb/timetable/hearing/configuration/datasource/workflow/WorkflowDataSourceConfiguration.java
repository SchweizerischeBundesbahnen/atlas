package ch.sbb.timetable.hearing.configuration.datasource.workflow;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class WorkflowDataSourceConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.workflow")
  public DataSourceProperties workflowDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "workflowDataSource")
  public DataSource workflowDataSource() {
    return workflowDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "workflowTransactionManager")
  public PlatformTransactionManager workflowTransactionManager() {
    return new DataSourceTransactionManager(workflowDataSource());
  }

}

