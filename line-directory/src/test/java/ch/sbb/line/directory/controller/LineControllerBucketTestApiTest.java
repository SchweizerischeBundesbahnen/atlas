package ch.sbb.line.directory.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.repository.CoverageRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.export.LineVersionExportService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

public class LineControllerBucketTestApiTest extends BaseControllerApiTest {

  @Autowired
  private LineController lineController;

  @Autowired
  private SublineController sublineController;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @Autowired
  private CoverageRepository coverageRepository;

  @Autowired
  private LineVersionExportService lineVersionExportService;

  @Autowired
  private LineVersionSnapshotRepository lineVersionSnapshotService;

  @AfterEach
  public void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
    coverageRepository.deleteAll();
    lineVersionSnapshotService.deleteAll();
  }

  @Test
  void shouldExportFullLineVersionsCsv() throws Exception {
    //given
    LineVersionModel lineVersionModel1 = LineTestData.lineVersionModelBuilder().build();
    LineVersionModel lineVersionModel2 = LineTestData.lineVersionModelBuilder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .description("descriptiön2")
        .build();
    lineController.createLineVersion(lineVersionModel1);
    lineController.createLineVersion(lineVersionModel2);

    //when
    MvcResult mvcResult = mvc.perform(post("/v1/lines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
//    deleteFileFromBucket(mvcResult, lineVersionExportService.getDirectory());
  }
}
