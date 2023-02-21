package ch.sbb.business.organisation.directory.service.crd;

import ch.sbb.business.organisation.directory.service.crd.CrdSoapClientConfig.CrdHeaders;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!integration-test")
public class CrdClientImpl implements CrdClient {

  private final WebServiceTemplate webServiceTemplate;
  private final CrdHeaders crdHeaders;

  @Override
  public List<Company> getAllCompanies() {
    ObjectFactory objectFactory = new ObjectFactory();
    CompanyRequest companyRequest = objectFactory.createCompanyRequest();
    CompanyReplicationRequest companyReplicationRequest = objectFactory.createCompanyReplicationRequest();
    companyRequest.setCompanyReplicationRequest(companyReplicationRequest);
    ReplicationVolume replicationVolume = objectFactory.createReplicationVolume();
    replicationVolume.setReplicateAll("ALL");
    companyReplicationRequest.setReplicationVolume(replicationVolume);

    CompanyDataResponse companyData = getCompanyData(
        objectFactory.createGetCompanyRequest(companyRequest));

    log.info("Received {} companies from CRD",
        companyData.getCompanyReplicationResponse().getCompany().size());
    return companyData.getCompanyReplicationResponse().getCompany();
  }

  @SuppressWarnings("unchecked")
  CompanyDataResponse getCompanyData(JAXBElement<CompanyRequest> getCompanyRequest) {
    return ((JAXBElement<CompanyDataResponse>) webServiceTemplate.marshalSendAndReceive(
        getCompanyRequest, crdHeaders)).getValue();
  }
}