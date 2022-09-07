package ch.sbb.atlas.user.administration.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GraphClientConfig {

  private final AzureConfig azureConfig;

  private static final String SCOPE_GRAPH_API = "https://graph.microsoft.com/.default";

  @Bean
  public GraphServiceClient<Request> initializeGraphServiceClient() {
    final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
        .clientId(azureConfig.getAppRegistrationId())
        .clientSecret(azureConfig.getAzureAdSecret())
        .tenantId(azureConfig.getTenantId())
        .build();
    final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(
        List.of(SCOPE_GRAPH_API), clientSecretCredential);
    return GraphServiceClient
        .builder()
        .authenticationProvider(tokenCredentialAuthProvider)
        .buildClient();
  }

}
