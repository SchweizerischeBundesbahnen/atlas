package ch.sbb.atlas.location.config.datasource.prm;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class PrmDataSourceConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.prm")
  public DataSourceProperties prmDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "prmDataSource")
  public DataSource prmDataSource() {
    return prmDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean(name = "prmTransactionManager")
  public JdbcTransactionManager prmTransactionManager() {
    return new JdbcTransactionManager(prmDataSource());
  }


  @Bean
  public JdbcTemplate prmJdbcTemplate(@Qualifier("prmDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }


}
