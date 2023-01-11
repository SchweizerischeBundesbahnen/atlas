package ch.sbb.importservice.service;

import ch.sbb.importservice.client.SePoDiClient;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SePoDiClientService extends BaseClientService {

  private final SePoDiClient sePoDiClient;

  public SePoDiClientService(SePoDiClient sePoDiClient) {
    this.sePoDiClient = sePoDiClient;
    this.clientName = "sePoDiClient";
  }

  public Response postServicePoints() {
    return executeRequest(sePoDiClient.postServicePoints(), "Full Line Versions CSV/ZIP");
  }

}
