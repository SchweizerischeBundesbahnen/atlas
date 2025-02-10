package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.bodi.TransportCompany;
import ch.sbb.exportservice.model.TransportCompanyCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TransportCompanyCsvProcessor extends BaseServicePointProcessor implements
    ItemProcessor<TransportCompany, TransportCompanyCsvModel> {

  @Override
  public TransportCompanyCsvModel process(TransportCompany transportCompany) {
    return TransportCompanyCsvModel.builder()
        .id(transportCompany.getId())
        .number(transportCompany.getNumber())
        .abbreviation(transportCompany.getAbbreviation())
        .description(transportCompany.getDescription())
        .businessRegisterName(transportCompany.getBusinessRegisterName())
        .transportCompanyStatus(transportCompany.getTransportCompanyStatus().name())
        .businessRegisterNumber(transportCompany.getBusinessRegisterNumber())
        .enterpriseId(transportCompany.getEnterpriseId())
        .ricsCode(transportCompany.getRicsCode())
        .businessOrganisationNumbers(transportCompany.getBusinessOrganisationNumbers())
        .comment(transportCompany.getComment())
        .creationDate(LOCAL_DATE_FORMATTER.format(transportCompany.getCreationDate()))
        .editionDate(LOCAL_DATE_FORMATTER.format(transportCompany.getEditionDate()))
        .build();
  }

}
