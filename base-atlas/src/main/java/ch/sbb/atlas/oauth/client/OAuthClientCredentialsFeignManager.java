package ch.sbb.atlas.oauth.client;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@RequiredArgsConstructor
public class OAuthClientCredentialsFeignManager {

  private final OAuth2AuthorizedClientManager manager;
  private final ClientRegistration clientRegistration;

  public String getAccessToken() {
    FeignAuthentication feignAuthentication = new FeignAuthentication(clientRegistration);
    OAuth2AuthorizeRequest oAuth2AuthorizeRequest =
        OAuth2AuthorizeRequest
            .withClientRegistrationId(clientRegistration.getRegistrationId())
            .principal(feignAuthentication)
            .build();
    OAuth2AuthorizedClient client = manager.authorize(oAuth2AuthorizeRequest);
    if (client == null) {
      throw new IllegalStateException(
          "client credentials flow on " + clientRegistration.getRegistrationId()
              + " failed, client is null");
    }
    return client.getAccessToken().getTokenValue();
  }

  @RequiredArgsConstructor
  public static class FeignAuthentication implements Authentication {

    @Serial
    private static final long serialVersionUID = 1;
    
    private final ClientRegistration clientRegistration;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.emptySet();
    }

    @Override
    public Object getCredentials() {
      return null;
    }

    @Override
    public Object getDetails() {
      return null;
    }

    @Override
    public Object getPrincipal() {
      return this;
    }

    @Override
    public boolean isAuthenticated() {
      return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
      return clientRegistration.getClientId();
    }

  }

}
