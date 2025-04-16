package ch.sbb.line.directory.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@MockitoBean(types = {AmazonService.class, SharedBusinessOrganisationService.class})
class SublineControllerInternalApiTest extends BaseControllerApiTest {

  private final LineControllerV2 lineControllerV2;
  private final SublineControllerV2 sublineControllerV2;
  private final LineVersionRepository lineVersionRepository;
  private final SublineVersionRepository sublineVersionRepository;

  @Autowired
  SublineControllerInternalApiTest(
      LineControllerV2 lineControllerV2,
      SublineControllerV2 sublineControllerV2,
      LineVersionRepository lineVersionRepository,
      SublineVersionRepository sublineVersionRepository) {
    this.lineControllerV2 = lineControllerV2;
    this.sublineControllerV2 = sublineControllerV2;
    this.lineVersionRepository = lineVersionRepository;
    this.sublineVersionRepository = sublineVersionRepository;
  }

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldExportFullSublineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel = lineControllerV2.createLineVersionV2(lineVersionModel);
    CreateSublineVersionModelV2 sublineVersionModel1 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    CreateSublineVersionModelV2 sublineVersionModel2 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 30))
        .description("desc2")
        .build();
    sublineControllerV2.createSublineVersionV2(sublineVersionModel1);
    sublineControllerV2.createSublineVersionV2(sublineVersionModel2);

    //when
    mvc.perform(post("/internal/sublines/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualSublineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel = lineControllerV2.createLineVersionV2(lineVersionModel);
    CreateSublineVersionModelV2 sublineVersionModel1 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(lineVersionModel.getSlnid())
        .build();
    CreateSublineVersionModelV2 sublineVersionModel2 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 1))
        .description("desc2")
        .build();
    sublineControllerV2.createSublineVersionV2(sublineVersionModel1);
    sublineControllerV2.createSublineVersionV2(sublineVersionModel2);

    //when
    mvc.perform(post("/internal/sublines/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportFutureTimetableLineVersionsCsv() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = LineTestData.createLineVersionModelBuilder().build();
    lineVersionModel = lineControllerV2.createLineVersionV2(lineVersionModel);
    CreateSublineVersionModelV2 sublineVersionModel1 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .build();
    CreateSublineVersionModelV2 sublineVersionModel2 = SublineTestData.createSublineVersionModelBuilderV2()
        .mainlineSlnid(
            lineVersionModel.getSlnid())
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 1))
        .description("desc2")
        .build();
    sublineControllerV2.createSublineVersionV2(sublineVersionModel1);
    sublineControllerV2.createSublineVersionV2(sublineVersionModel2);

    //when
    mvc.perform(post("/internal/sublines/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldRevokeSubline() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel = lineControllerV2.createLineVersionV2(
        LineTestData.createLineVersionModelBuilder().build());
    ReadSublineVersionModelV2 sublineVersionSaved = sublineControllerV2.createSublineVersionV2(
        SublineTestData.createSublineVersionModelBuilderV2()
            .mainlineSlnid(lineVersionModel.getSlnid())
            .build());

    //when
    mvc.perform(post("/internal/sublines/" + sublineVersionSaved.getSlnid() + "/revoke"))
        .andExpect(status().isOk());
  }
}
