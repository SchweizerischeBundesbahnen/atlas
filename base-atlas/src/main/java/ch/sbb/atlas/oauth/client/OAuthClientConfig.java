package ch.sbb.atlas.oauth.client;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@RequiredArgsConstructor
public class OAuthClientConfig {

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  private final String clientId;

  @Bean
  public RequestInterceptor authenticatingFeignRequestInterceptor() {
    ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientId);
    OAuthClientCredentialsFeignManager clientCredentialsFeignManager =
        new OAuthClientCredentialsFeignManager(authorizedClientManager(), clientRegistration);
    return requestTemplate -> requestTemplate
        .header("Authorization", "Bearer " + clientCredentialsFeignManager.getAccessToken());
  }

  OAuth2AuthorizedClientManager authorizedClientManager() {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials()
        .build();

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
        clientRegistrationRepository, oAuth2AuthorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }
}