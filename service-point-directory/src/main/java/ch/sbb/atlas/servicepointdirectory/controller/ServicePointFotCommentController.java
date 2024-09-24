package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointFotCommentApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointFotCommentMapper;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointFotCommentService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointFotCommentController implements ServicePointFotCommentApiV1 {

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
