package ch.sbb.atlas.gateway;

import java.util.Base64;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;

@Slf4j
@UtilityClass
class HeaderUtil {

  static String getClientCredentialId(HttpHeaders headers) {
    String authorizationHeader = getAuthorizationHeader(headers);
    String bearerToken = getBearerToken(authorizationHeader);
    return getJwtClaimFromToken(bearerToken, "azp");
  }

  private static String getAuthorizationHeader(HttpHeaders headers) {
    List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);
    if (authorization != null && authorization.size() == 1) {
      return authorization.getFirst();
    }
    return "";
  }

  private static String getBearerToken(String authorizationHeader) {
    if (authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    return "";
  }

  private static String getJwtClaimFromToken(String token, String claim) {
    try {
      String[] chunks = token.split("\\.");
      Base64.Decoder decoder = Base64.getUrlDecoder();
      String payload = new String(decoder.decode(chunks[1]));
      JSONObject jsonObject = new JSONObject(payload);
      return jsonObject.getString(claim);
    } catch (Exception e) {
      log.error("Could not read claim={} from token={}", claim, token, e);
      return "";
    }
  }

}
