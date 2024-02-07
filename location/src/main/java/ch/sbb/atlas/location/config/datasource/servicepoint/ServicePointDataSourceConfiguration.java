package ch.sbb.atlas.location.config.datasource.servicepoint;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class ServicePointDataSourceConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.service-point")
  public DataSourceProperties servicePointDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "servicePointDataSource")
  public DataSource servicePointDataSource() {
    return servicePointDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "servicePointTransactionManager")
  public JdbcTransactionManager servicePointTransactionManager() {
    return new JdbcTransactionManager(servicePointDataSource());
  }

  @Bean(name = "sePoDiJdbcTemplate")
  public NamedParameterJdbcTemplate sePoDiJdbcTemplate(@Qualifier("servicePointDataSource") DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }


}
