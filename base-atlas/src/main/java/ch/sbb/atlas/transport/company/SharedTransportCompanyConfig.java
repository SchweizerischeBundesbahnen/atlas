package ch.sbb.atlas.transport.company;

import ch.sbb.atlas.transport.company.repository.SharedTransportCompanyRepository;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyConsumer;
import ch.sbb.atlas.transport.company.service.SharedTransportCompanyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SharedTransportCompanyConfig {

  @Bean
  public SharedTransportCompanyConsumer sharedTransportCompanyConsumer(
      SharedTransportCompanyRepository sharedTransportCompanyRepository) {
    return new SharedTransportCompanyConsumer(sharedTransportCompanyRepository);
  }

  @Bean
  public SharedTransportCompanyService sharedTransportCompanyService(
      SharedTransportCompanyRepository sharedTransportCompanyRepository) {
    return new SharedTransportCompanyService(sharedTransportCompanyRepository);
  }

}
