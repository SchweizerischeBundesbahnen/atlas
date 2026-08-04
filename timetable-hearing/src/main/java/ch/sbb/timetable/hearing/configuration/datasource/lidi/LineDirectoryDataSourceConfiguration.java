package ch.sbb.timetable.hearing.configuration.datasource.lidi;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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
  public PlatformTransactionManager lineDirectoryTransactionManager() {
    return new DataSourceTransactionManager(lineDirectoryDataSource());
  }

}

