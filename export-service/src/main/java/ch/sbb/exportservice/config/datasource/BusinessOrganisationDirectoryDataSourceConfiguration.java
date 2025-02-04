package ch.sbb.exportservice.config.datasource;

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
  public DataSourceProperties transportCompanyDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "businessOrganisationDirectoryDataSource")
  public DataSource businessOrganisationDirectoryDataSource() {
    return transportCompanyDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "businessOrganisationDirectoryTransactionManager")
  public JdbcTransactionManager businessOrganisationDirectoryTransactionManager() {
    return new JdbcTransactionManager(businessOrganisationDirectoryDataSource());
  }

}
