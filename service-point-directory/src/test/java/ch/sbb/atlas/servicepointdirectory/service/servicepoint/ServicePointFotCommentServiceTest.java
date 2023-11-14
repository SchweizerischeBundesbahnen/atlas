package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class ServicePointFotCommentServiceTest {

  private static final int SERVICE_POINT_NUMBER = 85070003;

  private final ServicePointFotCommentService servicePointFotCommentService;

  @Autowired
   ServicePointFotCommentServiceTest(ServicePointFotCommentService servicePointFotCommentService) {
    this.servicePointFotCommentService = servicePointFotCommentService;
  }

  @Test
  void shouldSaveNewFotCommentToDb() {
    ServicePointFotComment comment = servicePointFotCommentService.save(
        ServicePointFotComment.builder().servicePointNumber(SERVICE_POINT_NUMBER).fotComment("Beste FOT").build());
    assertThat(comment).isNotNull();
  }

  @Test
  void shouldUpdateFotCommentBySavingWithSameId() {
    servicePointFotCommentService.save(
        ServicePointFotComment.builder().servicePointNumber(SERVICE_POINT_NUMBER).fotComment("Beste FOT").build());

    ServicePointFotComment comment = servicePointFotCommentService.save(
        ServicePointFotComment.builder().servicePointNumber(SERVICE_POINT_NUMBER).fotComment("Updated").build());
    assertThat(comment).isNotNull();
    assertThat(comment.getFotComment()).isEqualTo("Updated");
  }

  @Test
  void shouldDeleteFotCommentBySavingWithNull() {
    servicePointFotCommentService.save(
        ServicePointFotComment.builder().servicePointNumber(SERVICE_POINT_NUMBER).fotComment("Beste FOT").build());

    ServicePointFotComment comment = servicePointFotCommentService.save(
        ServicePointFotComment.builder().servicePointNumber(SERVICE_POINT_NUMBER).fotComment(null).build());

    assertThat(comment).isNull();
    assertThat(servicePointFotCommentService.findByServicePointNumber(SERVICE_POINT_NUMBER)).isEmpty();
  }
}