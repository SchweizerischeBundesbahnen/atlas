package ch.sbb.atlas.api.mail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

class MailTaskExecutorConfigTest {

  @Test
  void shouldExecuteMailRequestsOnManagedVirtualThreads() throws Exception {
    MailTaskExecutorConfig config = new MailTaskExecutorConfig();

    try (SimpleAsyncTaskExecutor taskExecutor = config.mailTaskExecutor()) {
      Future<Thread> thread = taskExecutor.submit(Thread::currentThread);
      Thread executorThread = thread.get();

      assertThat(executorThread.isVirtual()).isTrue();
      assertThat(executorThread.getName()).startsWith("mail-client-");
    }
  }

}
