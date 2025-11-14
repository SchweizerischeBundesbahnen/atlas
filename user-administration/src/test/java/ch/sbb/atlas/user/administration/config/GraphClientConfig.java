package ch.sbb.atlas.user.administration.config;

import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GraphClientConfig {

  @Bean
  public GraphServiceClient mockGraphServiceClient() {
    return Mockito.mock(GraphServiceClient.class);
  }
}