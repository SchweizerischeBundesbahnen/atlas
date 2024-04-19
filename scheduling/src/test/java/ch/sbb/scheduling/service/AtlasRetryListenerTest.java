package ch.sbb.scheduling.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

@ExtendWith(MockitoExtension.class)
class RetryListenerTest {

  @Mock
  private MailProducerService service;

  @Mock
  private MailNotificationService mailNotificationService;

  @InjectMocks
  @Spy
  private RetryListener retryListener;

  @Mock
  private RetryContext retryContext;

  @Mock
  private RetryCallback callback;

  private final Throwable throwable = new RuntimeException("Fatal exception");

  @Test
   void shouldSendEmailOnClose() {
    //given
    doReturn("export").when(retryListener).getJobName(any());

    //when
    retryListener.close(retryContext, callback, throwable);

    //then
    verify(mailNotificationService).buildMailNotification(any(), any());
    verify(service).produceMailNotification(any());
    verify(retryListener).close(retryContext, callback, throwable);
  }

  @Test
   void shouldCallOnError() {
    //given
    doReturn("export").when(retryListener).getJobName(any());

    //when
    retryListener.onError(retryContext, callback, throwable);

    //then
    verify(mailNotificationService, never()).buildMailNotification(any(), any());
    verify(service, never()).produceMailNotification(any());
    verify(retryListener).onError(retryContext, callback, throwable);
  }

}