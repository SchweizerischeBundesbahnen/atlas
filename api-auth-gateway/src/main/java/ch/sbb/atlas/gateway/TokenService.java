package ch.sbb.atlas.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class TokenService {

  private final ReactiveClientRegistrationRepository clientRegistrations;
  private final ReactiveOAuth2AuthorizedClientService clientService;

  public Mono<String> getClientCredentialAccessToken() {
    ReactiveOAuth2AuthorizedClientManager manager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
        clientRegistrations, clientService);
    return manager.authorize(getAuthorizateRequest())
        .map(oAuth2AuthorizedClient -> oAuth2AuthorizedClient.getAccessToken()
            .getTokenValue());
  }

  /**
   * OAuth2AuthorizeRequest to execute.
   * Configure your application with spring in application.yml as follows:
   *
   * spring:
   *  security:
   *    oauth2:
   *      client:
   *        registration:
   *          sbb:
   *            client-id: 39ff0746-6345-4204-afd3-3e114e5ab715
   *            client-secret: super-secret-text
   *            authorization-grant-type: client_credentials
   *            scope: api://87e6e634-6ba1-4e7a-869d-3348b4c3eafc/.default
   *         provider:
   *          sbb:
   *            token-uri: https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0/token
   *
   * The registrationId has to match the path in your settings.
   */
  private static OAuth2AuthorizeRequest getAuthorizateRequest() {
    return OAuth2AuthorizeRequest.withClientRegistrationId("sbb")
        .principal("unauthenticated")
        .build();
  }

}
