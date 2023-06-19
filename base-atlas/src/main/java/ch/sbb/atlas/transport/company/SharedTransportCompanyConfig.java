package ch.sbb.atlas.transport.company;

import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyConsumer;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedTransportCompanyConfig {

  @Bean
  public SharedTransportCompanyConsumer sharedTransportCompanyConsumer(
      TransportCompanySharingDataAccessor transportCompanySharingDataAccessor) {
    return new SharedTransportCompanyConsumer(transportCompanySharingDataAccessor);
  }

  @Bean
  public SharedTransportCompanyService sharedTransportCompanyService(
      TransportCompanySharingDataAccessor transportCompanySharingDataAccessor) {
    return new SharedTransportCompanyService(transportCompanySharingDataAccessor);
  }

}
