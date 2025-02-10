package ch.sbb.exportservice.config.datasource.bodi;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class BusinessOrganisationDirectoryDataSourceConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.bodi")
  public DataSourceProperties businessOrganisationDirectoryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "businessOrganisationDirectoryDataSource")
  @ConfigurationProperties("spring.datasource.bodi.hikari")
  public DataSource businessOrganisationDirectoryDataSource() {
    return businessOrganisationDirectoryDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "businessOrganisationDirectoryTransactionManager")
  public JdbcTransactionManager businessOrganisationDirectoryTransactionManager() {
    return new JdbcTransactionManager(businessOrganisationDirectoryDataSource());
  }

}
