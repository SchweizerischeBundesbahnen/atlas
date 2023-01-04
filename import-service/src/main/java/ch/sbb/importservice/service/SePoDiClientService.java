package ch.sbb.importservice.service;

import ch.sbb.importservice.client.SePoDiClient;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SePoDiClientService extends BaseClientService {

  private final SePoDiClient sePoDiClient;

  public SePoDiClientService(SePoDiClient liDiClient) {
    this.sePoDiClient = liDiClient;
    this.clientName = "LiDi-Client";
  }

  public Response exportFullLineVersions() {
    return executeRequest(sePoDiClient.putLiDiLineExportFull(), "Full Line Versions CSV/ZIP");
  }

}