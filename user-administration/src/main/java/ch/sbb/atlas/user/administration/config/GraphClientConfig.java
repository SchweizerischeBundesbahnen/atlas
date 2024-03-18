package ch.sbb.atlas.user.administration.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GraphClientConfig {

  private final AzureConfig azureConfig;

  private static final String SCOPE_GRAPH_API = "https://graph.microsoft.com/.default";

  @Bean
  public GraphServiceClient initializeGraphServiceClient() {
    final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
        .clientId(azureConfig.getAppRegistrationId())
        .clientSecret(azureConfig.getAzureAdSecret())
        .tenantId(azureConfig.getTenantId())
        .build();
    return new GraphServiceClient(clientSecretCredential, SCOPE_GRAPH_API);
  }

}
