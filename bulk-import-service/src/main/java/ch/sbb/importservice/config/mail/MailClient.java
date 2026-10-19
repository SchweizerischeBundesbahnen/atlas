package ch.sbb.importservice.config.mail;

import ch.sbb.atlas.api.mail.MailClientService;
import ch.sbb.atlas.api.mail.MailTaskExecutorConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
@Import(MailTaskExecutorConfig.class)
public class MailClient extends MailClientService {

  public MailClient(MailFeignClient mailFeignClient,
      @Qualifier(MailTaskExecutorConfig.MAIL_TASK_EXECUTOR) TaskExecutor mailTaskExecutor) {
    super(mailFeignClient, mailTaskExecutor);
  }
}
