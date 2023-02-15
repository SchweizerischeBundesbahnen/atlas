package ch.sbb.business.organisation.directory.service;

import java.io.ByteArrayOutputStream;
import jakarta.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

@Component
public class SoapMessageLoggerInterceptor extends ClientInterceptorAdapter {

  @Override
  public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
    SoapMessagePrettyPrinter.log(messageContext.getRequest());
    return true;
  }

  @Override
  public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
    SoapMessagePrettyPrinter.log(messageContext.getResponse());
    return false;
  }

  @Override
  public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
    SoapMessagePrettyPrinter.log(messageContext.getResponse());
    return true;
  }

  @Slf4j
  @UtilityClass
  public static class SoapMessagePrettyPrinter {

    public static void log(WebServiceMessage message) {
      if (message instanceof SaajSoapMessage saajSoapMessage) {
        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();

        try {
          Source source = soapMessage.getSOAPPart().getContent();

          Transformer transformer = createTransformer();
          ByteArrayOutputStream formattedMessage = new ByteArrayOutputStream();
          transformer.transform(source, new StreamResult(formattedMessage));

          log.info("\n{}", formattedMessage);

        } catch (Exception e) {
          throw new IllegalStateException(e);
        }

      }
    }

    private static Transformer createTransformer() throws TransformerConfigurationException {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      return transformer;
    }

  }
}