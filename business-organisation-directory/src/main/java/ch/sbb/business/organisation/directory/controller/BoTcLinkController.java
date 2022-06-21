package ch.sbb.business.organisation.directory.controller;


import ch.sbb.business.organisation.directory.api.BoTcLinkApiV1;
import ch.sbb.business.organisation.directory.api.BoTcLinkModel;
import ch.sbb.business.organisation.directory.entity.BoTcLink;
import ch.sbb.business.organisation.directory.service.BoTcLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class BoTcLinkController implements BoTcLinkApiV1 {

  private final BoTcLinkService boTcLinkService;

  @Override
  public BoTcLinkModel createBoTcLink(BoTcLinkModel model){
    BoTcLink entity = BoTcLinkModel.toEntity(model);
    BoTcLink savedEntity = boTcLinkService.save(entity);
    return BoTcLinkModel.toModel(savedEntity);
  }

}
