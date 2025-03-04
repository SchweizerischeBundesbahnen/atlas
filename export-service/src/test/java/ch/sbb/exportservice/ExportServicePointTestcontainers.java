package ch.sbb.exportservice;

import ch.sbb.atlas.model.controller.PostgreSQLTestContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@Component
class ExportServicePointTestcontainers implements BeanFactoryPostProcessor {

  static PostgreSQLContainer<?> servicePointDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> prmDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> bodiDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> lidiDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> exportDbContainer = PostgreSQLTestContainer.create();

  static {
    Startables.deepStart(servicePointDbContainer, prmDbContainer, bodiDbContainer, lidiDbContainer, exportDbContainer).join();
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.batch.datasource", exportDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.service-point", servicePointDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.prm", prmDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.bodi", bodiDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.lidi", lidiDbContainer);
  }
}