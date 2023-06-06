package ch.sbb.exportservice.config.datasource.servicepoint;

import jakarta.persistence.PersistenceContext;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"ch.sbb.exportservice"},
    entityManagerFactoryRef = "servicePointEntityManagerFactory",
    transactionManagerRef = "servicePointTransactionManager"
)
public class ServicePointJpaDataSource {

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

  @PersistenceContext
  @Bean
  public LocalContainerEntityManagerFactoryBean servicePointEntityManagerFactory(
      @Qualifier("servicePointDataSource") DataSource dataSource,
      EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(dataSource)
        .packages("ch.sbb.exportservice")
        .build();
  }

  @Bean
  public PlatformTransactionManager servicePointTransactionManager(
      @Qualifier("servicePointEntityManagerFactory") LocalContainerEntityManagerFactoryBean servicePointEntityManagerFactory) {
    return new JpaTransactionManager(Objects.requireNonNull(servicePointEntityManagerFactory.getObject()));
  }

}
