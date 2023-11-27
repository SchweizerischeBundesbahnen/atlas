package ch.sbb.atlas.oauth.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;

@ExtendWith(MockitoExtension.class)
 class OAuthClientCredentialsFeignManagerTest {

  private OAuthClientCredentialsFeignManager oAuthClientCredentialsFeignManager;

  @Mock
  private OAuth2AuthorizedClientManager manager;

  @Mock
  private ClientRegistration clientRegistration;
  @Mock
  private OAuth2AuthorizedClient client;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);

    oAuthClientCredentialsFeignManager = new OAuthClientCredentialsFeignManager(manager,
        clientRegistration);
  }

  @Test
   void shouldThrowIllegaleStateExceptionWhenAuthenticationFailed() {
    //given
    when(clientRegistration.getRegistrationId()).thenReturn("azure");

    //when
    assertThrows(IllegalStateException.class,
        () -> oAuthClientCredentialsFeignManager.getAccessToken());
  }

  @Test
   void shouldGetToken() {
    //given
    when(clientRegistration.getRegistrationId()).thenReturn("id");
    when(manager.authorize(any())).thenReturn(client);
    OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(TokenType.BEARER, "token",
        Instant.now(), Instant.now().plusSeconds(6000));
    doReturn(oAuth2AccessToken).when(client).getAccessToken();

    //when
    String result = oAuthClientCredentialsFeignManager.getAccessToken();

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("token");
  }

}