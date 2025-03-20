package ch.sbb.exportservice.job.bodi.transportcompany.processor;

import ch.sbb.exportservice.job.bodi.transportcompany.entity.TransportCompany;
import ch.sbb.exportservice.job.bodi.transportcompany.model.TransportCompanyCsvModel;
import ch.sbb.exportservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TransportCompanyCsvProcessor implements ItemProcessor<TransportCompany, TransportCompanyCsvModel> {

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
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(transportCompany.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(transportCompany.getEditionDate()))
        .build();
  }

}
