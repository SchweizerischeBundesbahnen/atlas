package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServicePointFotCommentService {

  private final ServicePointFotCommentRepository servicePointFotCommentRepository;

  public ServicePointFotComment save(ServicePointFotComment fotComment) {
    return servicePointFotCommentRepository.save(fotComment);
  }

  public Optional<ServicePointFotComment> findByServicePointNumber(Integer servicePointNumber) {
    return servicePointFotCommentRepository.findById(servicePointNumber);
  }

  public void importFotComment(ServicePointFotComment fotComment) {
    if (StringUtils.isNotBlank(fotComment.getFotComment())) {
      save(fotComment);
    }
  }
}
