package ch.sbb.workflow.mail;

import ch.sbb.atlas.api.mail.MailClientService;
import ch.sbb.atlas.api.mail.MailTaskExecutorConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Import(MailTaskExecutorConfig.class)
public class MailClient extends MailClientService {

  public MailClient(MailFeignClient mailFeignClient, ObjectMapper objectMapper,
      @Value("${atlas.client.gateway.url}") String gatewayUrl,
      @Qualifier(MailTaskExecutorConfig.MAIL_TASK_EXECUTOR) TaskExecutor mailTaskExecutor) {
    super(mailFeignClient, objectMapper, gatewayUrl, mailTaskExecutor);
  }
}
