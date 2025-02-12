package ch.sbb.exportservice.config.datasource.lidi;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class LineDirectoryDataSourceConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.lidi")
  public DataSourceProperties lineDirectoryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "lineDirectoryDataSource")
  public DataSource lineDirectoryDataSource() {
    return lineDirectoryDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "lineDirectoryTransactionManager")
  public JdbcTransactionManager businessOrganisationDirectoryTransactionManager() {
    return new JdbcTransactionManager(lineDirectoryDataSource());
  }

}
