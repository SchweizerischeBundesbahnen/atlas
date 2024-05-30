package ch.sbb.workflow.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetreiveMessageErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    //TODO: implement better client Error Handling
    throw new IllegalStateException("Something went wrong! SePoDi client return status: " + response.status());
  }
}