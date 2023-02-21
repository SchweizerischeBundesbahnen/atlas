package ch.sbb.business.organisation.directory.service.crd;

import ch.sbb.business.organisation.directory.service.SoapMessageLoggerInterceptor;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

@Slf4j
@RequiredArgsConstructor
@Configuration
@Profile("!integration-test")
public class CrdSoapClientConfig {

  public static final String MARSHALL_CONTEXT_PATH = "ch.sbb.business.organisation.directory.service.crd";

  private final CrdSoapConfig config;
  private final SoapMessageLoggerInterceptor soapMessageLoggerInterceptor;

  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(MARSHALL_CONTEXT_PATH);
    return marshaller;
  }

  @Bean
  public Wss4jSecurityInterceptor securityInterceptor() throws Exception {
    Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
    wss4jSecurityInterceptor.setSecurementActions("Signature");

    wss4jSecurityInterceptor.setSecurementUsername(config.getKeystoreAlias());
    wss4jSecurityInterceptor.setSecurementPassword(config.getKeystorePassword());
    wss4jSecurityInterceptor.setSecurementSignatureParts(
        "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
    wss4jSecurityInterceptor.setSecurementSignatureCrypto(getCryptoFactoryBean().getObject());
    return wss4jSecurityInterceptor;
  }

  @Bean
  public CryptoFactoryBean getCryptoFactoryBean() throws IOException {
    CryptoFactoryBean cryptoFactoryBean = new CryptoFactoryBean();
    cryptoFactoryBean.setKeyStoreLocation(config.getKeystore());
    cryptoFactoryBean.setKeyStorePassword(config.getKeystorePassword());
    return cryptoFactoryBean;
  }

  @Bean
  public WebServiceTemplate webServiceTemplate(WebServiceTemplateBuilder builder) throws Exception {
    return builder.setDefaultUri(config.getUrl())
                  .setMarshaller(marshaller())
                  .setUnmarshaller(marshaller())
                  .additionalMessageSenders(createWebServiceMessageSender())
                  // LoggingInterceptor for Debugging purposes
                  .additionalInterceptors(securityInterceptor()/*, soapMessageLoggerInterceptor*/)
                  .build();
  }

  private WebServiceMessageSender createWebServiceMessageSender() {
    HttpsUrlConnectionMessageSender webServiceMessageSender = new HttpsUrlConnectionMessageSender();
    webServiceMessageSender.setKeyManagers(createKeyManagerFactory().getKeyManagers());
    webServiceMessageSender.setTrustManagers(createTrustManagerFactory().getTrustManagers());
    webServiceMessageSender.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
    return webServiceMessageSender;
  }

  private KeyManagerFactory createKeyManagerFactory() {
    try {
      KeyStore keystore = KeyStore.getInstance("JKS");
      try (InputStream keyStoreInputStream = config.getKeystore().getInputStream()) {
        keystore.load(keyStoreInputStream, config.getKeystorePassword().toCharArray());
      }

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keystore, config.getKeystorePassword().toCharArray());
      return keyManagerFactory;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private TrustManagerFactory createTrustManagerFactory() {
    try {
      KeyStore truststore = KeyStore.getInstance("JKS");
      try (InputStream trustStoreInputStream = config.getTruststore().getInputStream()) {
        truststore.load(trustStoreInputStream, config.getTruststorePassword().toCharArray());
      }

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(truststore);
      return trustManagerFactory;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Bean
  public CrdHeaders crdHeaders() {
    return new CrdHeaders(config);
  }

  @RequiredArgsConstructor
  static class CrdHeaders implements WebServiceMessageCallback {

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

}