package ch.sbb.atlas.location;

import ch.sbb.atlas.model.controller.PostgreSQLTestContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@Component
class LocationTestcontainers implements BeanFactoryPostProcessor {

  static PostgreSQLContainer<?> servicePointDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> prmDbContainer = PostgreSQLTestContainer.create();
  static PostgreSQLContainer<?> locationDbContainer = PostgreSQLTestContainer.create();

  static {
    Startables.deepStart(servicePointDbContainer, prmDbContainer, locationDbContainer).join();
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.location", locationDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.service-point", servicePointDbContainer);
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource.prm", prmDbContainer);
  }
}