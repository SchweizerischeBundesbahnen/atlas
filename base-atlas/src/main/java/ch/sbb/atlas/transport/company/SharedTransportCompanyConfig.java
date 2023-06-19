package ch.sbb.atlas.transport.company;

import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedTransportCompanyConfig {

  private final TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  @Autowired
  public SharedTransportCompanyConfig(TransportCompanySharingDataAccessor transportCompanySharingDataAccessor) {
    this.transportCompanySharingDataAccessor = transportCompanySharingDataAccessor;
  }

  @Bean
  public SharedTransportCompanyConsumer sharedTransportCompanyConsumer() {
    return new SharedTransportCompanyConsumer(this.transportCompanySharingDataAccessor);
  }

}
