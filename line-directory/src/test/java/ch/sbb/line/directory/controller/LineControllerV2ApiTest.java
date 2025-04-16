package ch.sbb.line.directory.controller;

import static ch.sbb.atlas.api.lidi.BaseLineVersionModel.Fields.businessOrganisation;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2.Fields;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV2ApiTest extends BaseControllerApiTest {

  private final LineControllerV2 lineControllerV2;
  private final LineVersionRepository lineVersionRepository;

  @Autowired
  LineControllerV2ApiTest(
      LineControllerV2 lineControllerV2,
      LineVersionRepository lineVersionRepository) {
    this.lineControllerV2 = lineControllerV2;
    this.lineVersionRepository = lineVersionRepository;
  }

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldGetLineVersion() throws Exception {
    //given
    LineVersion lineVersion = LineTestData.lineVersionV2Builder().build();
    lineVersionRepository.saveAndFlush(lineVersion);

    //when
    mvc.perform(get("/v2/lines/versions/" + lineVersion.getSlnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldGetLineVersionsNotFound() throws Exception {
    mvc.perform(get("/v2/lines/versions/123"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateLineVersion() throws Exception {
    //given
    LineVersionModelV2 lineVersionModel =
        LineTestData.createLineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .longName("long name")
            .lineType(LineType.ORDERLY)
            .build();
    //when && then
    mvc.perform(post("/v2/lines/versions")
        .contentType(contentType)
        .content(mapper.writeValueAsString(lineVersionModel))
    ).andExpect(status().isCreated());
  }

  @Test
  void shouldUpdateLineVersion() throws Exception {
    //given
    LineVersionModelV2 createLineVersionModelV2 =
        LineTestData.createLineVersionModelBuilder()
            .businessOrganisation("sbb")
            .longName("long name")
            .lineType(LineType.ORDERLY)
            .swissLineNumber("b0.IC6")
            .lineConcessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
            .build();

    LineVersionModelV2 lineVersionSaved = lineControllerV2.createLineVersionV2(createLineVersionModelV2);

    UpdateLineVersionModelV2 updateLineVersionModelV2 =
        LineTestData.updateLineVersionModelBuilder()
            .businessOrganisation("PostAuto")
            .longName("long name")
            .swissLineNumber("b0.IC2")
            .id(lineVersionSaved.getId())
            .etagVersion(lineVersionSaved.getEtagVersion())
            .editor(lineVersionSaved.getEditor())
            .editionDate(lineVersionSaved.getEditionDate())
            .creator(lineVersionSaved.getCreator())
            .creationDate(lineVersionSaved.getCreationDate())
            .build();

    //when
    mvc.perform(put("/v2/lines/versions/" + lineVersionSaved.getId().toString())
            .contentType(contentType)
            .content(mapper.writeValueAsString(updateLineVersionModelV2))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + UpdateLineVersionModelV2.Fields.swissLineNumber, is("b0.IC2")))
        .andExpect(jsonPath("$[0]." + Fields.lineType, is(LineType.ORDERLY.toString())))
        .andExpect(jsonPath("$[0]." + businessOrganisation, is("PostAuto")));
  }

}
