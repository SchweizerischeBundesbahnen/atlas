package ch.sbb.atlas.servicepointdirectory.module.servicepoint.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointFotCommentApiV1;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper.ServicePointFotCommentMapper;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointFotCommentApiV1Controller implements ServicePointFotCommentApiV1 {

  private final ServicePointService servicePointService;
  private final ServicePointFotCommentService servicePointFotCommentService;

  @Override
  public Optional<ServicePointFotCommentModel> getFotComment(Integer servicePointNumber) {
    return servicePointFotCommentService.findByServicePointNumber(servicePointNumber).map(ServicePointFotCommentMapper::toModel);
  }

  @Override
  public ServicePointFotCommentModel saveFotComment(Integer servicePointNumber, ServicePointFotCommentModel fotComment) {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber);
    if (!servicePointService.isServicePointNumberExisting(number)) {
      throw new ServicePointNumberNotFoundException(number);
    }

    ServicePointFotComment entity = ServicePointFotCommentMapper.toEntity(fotComment, number);
    return ServicePointFotCommentMapper.toModel(servicePointFotCommentService.save(entity));
  }

}
