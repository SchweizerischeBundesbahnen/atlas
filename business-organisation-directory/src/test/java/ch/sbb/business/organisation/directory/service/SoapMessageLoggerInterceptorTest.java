package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl;
import org.junit.jupiter.api.Test;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

class SoapMessageLoggerInterceptorTest {

  @Test
  void shouldHandleRequestByLoggingItPretty() {
    // When
    boolean result = new SoapMessageLoggerInterceptor().handleRequest(messageContext());

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void shouldHandleResponsesByLoggingItPretty() {
    // When
    boolean result = new SoapMessageLoggerInterceptor().handleResponse(messageContext());

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldHandleFaultByLoggingItPretty() {
    // When
    boolean result = new SoapMessageLoggerInterceptor().handleFault(messageContext());

    // Then
    assertThat(result).isTrue();
  }

  private static MessageContext messageContext() {
    MessageContext messageContext = mock(MessageContext.class);
    when(messageContext.getRequest()).thenReturn(new SaajSoapMessage(new Message1_2Impl()));
    return messageContext;
  }
}