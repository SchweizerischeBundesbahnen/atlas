package ch.sbb.atlas.location.config.datasource.location;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class LocationDataSourceConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.location")
  public DataSourceProperties locationDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "locationDataSource")
  @Primary
  public DataSource locationDataSource() {
    return locationDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Primary
  @Bean(name = "locationTransactionManager")
  public JdbcTransactionManager batchTransactionManager() {
    return new JdbcTransactionManager(locationDataSource());
  }

  @Bean(name = "locationJdbcTemplate")
  public JdbcTemplate locationJdbcTemplate(@Qualifier("locationDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

}
