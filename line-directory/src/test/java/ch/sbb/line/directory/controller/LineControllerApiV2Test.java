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
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class LineControllerApiV2Test extends BaseControllerApiTest {

  @Autowired
  private LineControllerV2 lineControllerV2;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
    sublineVersionRepository.deleteAll();
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
            .lineConcessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
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

  @Test
  void shouldCheckAffectedSublines() throws Exception {
    LineVersion lineVersion = LineTestData.lineVersionV2Builder().build();
    lineVersion.setValidFrom(LocalDate.of(1999, 1, 1));
    lineVersion.setValidTo(LocalDate.of(2020, 12, 31));
    lineVersion.setBusinessOrganisation("ch:1:sboid:1100000");
    lineVersion.setSlnid("ch:1:slnid:1000000");
    LineVersion saved = lineVersionRepository.saveAndFlush(lineVersion);

    String validFrom = "1999-01-01";
    String validTo = "2019-12-31";

    SublineVersion subline = SublineTestData.sublineVersionV2Builder().build();
    subline.setValidFrom(LocalDate.of(1999, 1, 1));
    subline.setValidTo(LocalDate.of(2020, 12, 31));
    subline.setSlnid("ch:1:slnid:1000000:1");
    subline.setBusinessOrganisation("ch:1:sboid:1100000");
    sublineVersionRepository.saveAndFlush(subline);

    mvc.perform(get("/v2/lines/affectedSublines/" + saved.getId())
            .param("validFrom", validFrom)
            .param("validTo", validTo))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.allowedSublines").isArray())
        .andExpect(jsonPath("$.notAllowedSublines").isArray())
        .andExpect(jsonPath("$.affectedSublinesEmpty").exists())
        .andExpect(jsonPath("$.hasAllowedSublinesOnly").exists())
        .andExpect(jsonPath("$.hasNotAllowedSublinesOnly").exists());
  }
}
