package ch.sbb.exportservice.config.datasource.servicepoint;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        .build();
  }

}
