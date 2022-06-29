package ch.sbb.business.organisation.directory.service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

@RequiredArgsConstructor
@Component
public class CrdHeaders implements WebServiceMessageCallback {

  private final CrdSoapConfig config;

  @Override
  public void doWithMessage(WebServiceMessage message) {
    SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();

    ObjectFactory objectFactory = new ObjectFactory();
    JAXBElement<String> username = objectFactory.createUsername(config.getUsername());
    JAXBElement<String> password = objectFactory.createPassword(config.getPassword());

    try {
      JAXBContext context = JAXBContext.newInstance(String.class);

      Marshaller marshaller = context.createMarshaller();
      marshaller.marshal(username, soapHeader.getResult());
      marshaller.marshal(password, soapHeader.getResult());
    } catch (JAXBException e) {
      throw new IllegalStateException(e);
    }
  }
}
