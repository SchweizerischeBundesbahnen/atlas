package ch.sbb.timetable.hearing;

import ch.sbb.atlas.model.controller.PostgreSQLTestContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Component
class DatabaseTestcontainers implements BeanFactoryPostProcessor {

  static PostgreSQLContainer tthDbContainer = PostgreSQLTestContainer.create();

  static {
    Startables.deepStart(tthDbContainer).join();
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource", tthDbContainer);
  }
}
