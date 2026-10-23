package ch.sbb.line.directory.module.line.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Set;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV3ApiTest extends BaseControllerApiTest {

  private static final String VERSIONS_URL = "/v3/lines/versions";

  private final LineVersionV3Repository lineVersionV3Repository;
  private final EntityManagerFactory entityManagerFactory;

  @Autowired
  LineControllerV3ApiTest(LineVersionV3Repository lineVersionV3Repository, EntityManagerFactory entityManagerFactory) {
    this.lineVersionV3Repository = lineVersionV3Repository;
    this.entityManagerFactory = entityManagerFactory;
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
  }

  @Test
  void shouldReturnContainerShapeWithoutAuthentication() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)))
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldReturnTypeLineWhenReferencedLineSlnidIsNull() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0].type", is(LineObjectType.LINE.name())))
        .andExpect(jsonPath("$.objects[0].referencedLine", is(nullValue())));
  }

  @Test
  void shouldReturnTypeSublineWithResolvedReferencedLine() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CHILD_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type", is(LineObjectType.SUBLINE.name())))
        .andExpect(jsonPath("$[0].referencedLine.lineSlnid", is(LineVersionV3TestData.PARENT_SLNID)));
  }

  @Test
  void shouldExposeFotCommentIndependentOfCommentOnPage() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().comment("TU comment").fotComment("BAV comment").build());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0].comment", is("TU comment")))
        .andExpect(jsonPath("$.objects[0].fotComment", is("BAV comment")));
  }

  @Test
  void shouldExposeFotCommentBySlnid() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().fotComment("BAV comment").build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fotComment", is("BAV comment")))
        .andExpect(jsonPath("$[0].comment", is(nullValue())));
  }

  @Test
  void shouldReturnNullFotCommentWhenUnset() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().comment("TU comment").build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fotComment", is(nullValue())))
        .andExpect(jsonPath("$[0].comment", is("TU comment")));
  }

  @Test
  void shouldReturnFotCommentOfMaxLengthWithoutTruncation() throws Exception {
    //given
    String fotComment = "b".repeat(AtlasFieldLengths.LENGTH_1500);
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().fotComment(fotComment).build());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0].fotComment", is(fotComment)));
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].fotComment", is(fotComment)));
  }

  @Test
  void shouldNotChangeQueryCountPerPageWhenFotCommentIsSet() throws Exception {
    //given
    Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    statistics.setStatisticsEnabled(true);
    try {
      saveLineWithTwoMembers(null);
      long queryCountWithoutFotComment = countStatementsOfVersionsPage(statistics);
      lineVersionV3Repository.deleteAll();

      saveLineWithTwoMembers("b".repeat(AtlasFieldLengths.LENGTH_1500));

      //when
      long queryCountWithFotComment = countStatementsOfVersionsPage(statistics);

      //then
      assertThat(queryCountWithFotComment).isPositive().isEqualTo(queryCountWithoutFotComment);
    } finally {
      statistics.setStatisticsEnabled(false);
    }
  }

  private void saveLineWithTwoMembers(String fotComment) {
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().fotComment(fotComment).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder().fotComment(fotComment).build());
    lineVersionV3Repository.saveAndFlush(
        LineVersionV3TestData.sublineBuilder().slnid("ch:1:slnid:1028629:5").fotComment(fotComment).build());
  }

  private long countStatementsOfVersionsPage(Statistics statistics) throws Exception {
    statistics.clear();
    mvc.perform(get(VERSIONS_URL)).andExpect(status().isOk());
    return statistics.getPrepareStatementCount();
  }

  @Test
  void shouldReturnEmptyContainerWhenTableIsEmpty() throws Exception {
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(0)))
        .andExpect(jsonPath("$.totalCount", is(0)));
  }

  @Test
  void shouldReturnFirstPageOfThreeRows() throws Exception {
    //given
    saveThreeLineVersions();

    //when && then
    mvc.perform(get(VERSIONS_URL + "?page=0&size=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(2)))
        .andExpect(jsonPath("$.totalCount", is(3)));
  }

  @Test
  void shouldReturnEmptyObjectsOnOutOfRangePage() throws Exception {
    //given
    saveThreeLineVersions();

    //when && then
    mvc.perform(get(VERSIONS_URL + "?page=5&size=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(0)))
        .andExpect(jsonPath("$.totalCount", is(3)));
  }

  @Test
  void shouldFilterBySwissLineNumber() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().swissLineNumber("90.583").build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .slnid("ch:1:slnid:2000001")
        .swissLineNumber("IC61")
        .build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "?swissLineNumber=90.583"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].swissLineNumber", is("90.583")));
  }

  @Test
  void shouldFilterByStatusRestrictions() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().status(Status.VALIDATED).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .slnid("ch:1:slnid:2000001")
        .status(Status.REVOKED)
        .build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "?statusRestrictions=VALIDATED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].status", is(Status.VALIDATED.name())));
  }

  @Test
  void shouldFilterByTypeRestrictions() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when && then
    mvc.perform(get(VERSIONS_URL + "?typeRestrictions=SUBLINE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].type", is(LineObjectType.SUBLINE.name())));
  }

  @Test
  void shouldRejectUnknownEnumRequestParam() throws Exception {
    mvc.perform(get(VERSIONS_URL + "?statusRestrictions=NOT_AN_ENUM"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())));
  }

  @Test
  void shouldRejectMalformedDateRequestParam() throws Exception {
    mvc.perform(get(VERSIONS_URL + "?validOn=31-12-2026"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())));
  }

  @Test
  void shouldReturnVersionsOrderedByValidFromAscending() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 5, 7))
        .build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 5, 6))
        .build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].validFrom", is("2026-01-01")))
        .andExpect(jsonPath("$[1].validFrom", is("2026-05-07")));
  }

  @Test
  void shouldReturnNotFoundForUnknownSlnid() throws Exception {
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.UNKNOWN_SLNID))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(jsonPath("$.details[0].field", is("slnid")));
  }

  @Test
  void shouldReturnNullReferencedLineWhenParentIsMissing() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CHILD_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type", is(LineObjectType.SUBLINE.name())))
        .andExpect(jsonPath("$[0].referencedLine", is(nullValue())));
  }

  @Test
  void shouldResolveReferencedLineFromRevokedParent() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CHILD_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].referencedLine.lineSlnid", is(LineVersionV3TestData.PARENT_SLNID)))
        .andExpect(jsonPath("$[0].referencedLine.swissLineNumber", is("90.583")));
  }

  @Test
  void shouldExposeConcessionTypeOfReferencedLineOnlyInsideReferencedLine() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CHILD_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].concessionType").doesNotExist())
        .andExpect(jsonPath("$[0].offerCategory").doesNotExist())
        .andExpect(jsonPath("$[0].referencedLine.concessionType", is(ConcessionType.CANTONALLY_APPROVED_LINE.name())))
        .andExpect(jsonPath("$[0].referencedLine.offerCategory", is(OfferCategory.B.name())));
  }

  @Test
  void shouldNotExposeWorkflowsNorShortNumber() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0].lineVersionWorkflows").doesNotExist())
        .andExpect(jsonPath("$.objects[0].shortNumber").doesNotExist());
  }

  @Test
  void shouldExposeOwnMeansOfTransportAndFinancingOnSubline() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .build());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CHILD_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].meansOfTransport", hasSize(1)))
        .andExpect(jsonPath("$[0].meansOfTransport[0]", is(LineMeanOfTransport.BUS.name())))
        .andExpect(jsonPath("$[0].financing", is("WBV")));
  }

  @Test
  void shouldExposeCablewayAttributes() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.cableway());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.CABLEWAY_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cablewayNumber", is("3521")))
        .andExpect(jsonPath("$[0].cablewayType", is("Pendelbahn")))
        .andExpect(jsonPath("$[0].cablewayName", is("Luftseilbahn Wengen - Maennlichen")));
  }

  @Test
  void shouldReturnNullCablewayAttributesForNonCablewayLine() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cablewayNumber", is(nullValue())))
        .andExpect(jsonPath("$[0].cablewayType", is(nullValue())))
        .andExpect(jsonPath("$[0].cablewayName", is(nullValue())));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldGetLineVersionsAsUnauthorized() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldGetLineVersionsBySlnidAsUnauthorized() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL + "/" + LineVersionV3TestData.PARENT_SLNID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldGetLineVersionsAsStandardUser() throws Exception {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //when && then
    mvc.perform(get(VERSIONS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  private void saveThreeLineVersions() {
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:3000001").build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:3000002").build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder().slnid("ch:1:slnid:3000003").build());
  }

}
