package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.exportservice.entity.bodi.TransportCompany;
import ch.sbb.exportservice.entity.prm.StopPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TransportCompanyJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<TransportCompany,
    TransportCompanyModel> {

  @Override
  public TransportCompanyModel process(TransportCompany transportCompany) {
    return TransportCompanyModel.builder()
        .id(transportCompany.getId())
        .number(transportCompany.getNumber())
        .build();
  }

}
