package ch.sbb.exportservice.config.datasource.batch;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class BatchDataSourceConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties("spring.batch.datasource")
  public DataSourceProperties batchDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "batchDataSource")
  @Primary
  public DataSource batchDataSource() {
    return batchDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Primary
  @Bean(name = "batchTransactionManager")
  public JdbcTransactionManager batchTransactionManager() {
    return new JdbcTransactionManager(batchDataSource());
  }

}
