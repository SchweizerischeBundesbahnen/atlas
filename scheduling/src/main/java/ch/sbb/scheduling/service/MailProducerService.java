package ch.sbb.scheduling.service;

import ch.sbb.atlas.api.mail.MailClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class MailProducerService extends MailClientService {

  public MailProducerService(MailFeignClient mailFeignClient, ObjectMapper objectMapper,
      @Value("${atlas.client.gateway.url}") String gatewayUrl) {
    super(mailFeignClient, objectMapper, gatewayUrl);
  }
}
