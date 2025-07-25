package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class HeaderUtilTest {

  @Test
  void shouldGetClientCredentialIdFromHeaderToken() {
    String testToken = """
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiYXpwIjoiY2xpZW50LWlkIn0.eu_Mul27GApW1wynoJiWTQwKx93S55obV6Fq2H661Jw""";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(testToken);
    String clientCredentialId = HeaderUtil.getClientCredentialId(headers);

    assertThat(clientCredentialId).isEqualTo("client-id");
  }

  @Test
  void shouldGetEmptyClientCredentialIdFromInvalidHeaderToken() {
    String testToken = """
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30""";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(testToken);
    String clientCredentialId = HeaderUtil.getClientCredentialId(headers);

    assertThat(clientCredentialId).isEmpty();
  }

  @Test
  void shouldGetEmptyClientCredentialIdFromNoAuthorizationHeaderToken() {
    HttpHeaders headers = new HttpHeaders();
    String clientCredentialId = HeaderUtil.getClientCredentialId(headers);

    assertThat(clientCredentialId).isEmpty();
  }

}