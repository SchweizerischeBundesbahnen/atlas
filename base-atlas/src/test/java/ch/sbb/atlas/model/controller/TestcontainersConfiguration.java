package ch.sbb.atlas.model.controller;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  @Bean
  public GeneralServiceTestContainer generalServiceTestContainer() {
    return new GeneralServiceTestContainer();
  }

  public static class GeneralServiceTestContainer implements BeanFactoryPostProcessor {

    public static PostgreSQLContainer<?> postgreSQLContainer = PostgreSQLTestContainer.create();

    static {
      Startables.deepStart(postgreSQLContainer).join();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      PostgreSQLTestContainer.setSystemPropertiesForDatasource("spring.datasource", postgreSQLContainer);
    }
  }

}