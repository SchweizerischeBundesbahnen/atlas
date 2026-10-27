package ch.sbb.line.directory.module.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3MembersOutsideValidityException;
import ch.sbb.line.directory.module.line.exception.OrderlyLineValidityException;
import ch.sbb.line.directory.module.line.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Write flow of {@link LineVersionV3Service} against the real DB and versioning engine. Deliberately not
 * {@code @Transactional}, so the service's own transaction commits or rolls back.
 */
@IntegrationTest
class LineVersionV3ServiceWriteIntegrationTest {

  private static final LocalDate VALID_FROM = LocalDate.of(2020, 1, 1);
  private static final LocalDate VALID_TO = LocalDate.of(2020, 12, 31);
  private static final String SWISS_LINE_NUMBER = "WP7.1";
  private static final String DESCRIPTION = "Val-Surses-Bus";

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final LineVersionV3Service lineVersionV3Service;
  private final LineVersionV3Repository lineVersionV3Repository;

  @Autowired
  LineVersionV3ServiceWriteIntegrationTest(LineVersionV3Service lineVersionV3Service,
      LineVersionV3Repository lineVersionV3Repository) {
    this.lineVersionV3Service = lineVersionV3Service;
    this.lineVersionV3Repository = lineVersionV3Repository;
  }

  @AfterEach
  void cleanUp() {
    lineVersionV3Repository.deleteAll();
  }

  @Test
  void shouldGenerateSlnidAndSetStatusValidatedWhenCreatingOrderlyLine() {
    // When
    LineVersionV3 created = lineVersionV3Service.create(newLine().build());

    // Then
    List<LineVersionV3> stored = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(created.getSlnid());
    assertThat(created.getSlnid()).matches("ch:1:slnid:\\d+");
    assertThat(stored)
        .extracting(LineVersionV3::getStatus, LineVersionV3::getLineType, LineVersionV3::getReferencedLineSlnid)
        .containsExactly(tuple(Status.VALIDATED, LineType.ORDERLY, null));
  }

  @Test
  void shouldStoreMemberWithReferencedLineSlnidAndWithoutParentResolvedFields() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());

    // When
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));

    // Then
    List<LineVersionV3> stored = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(member.getSlnid());
    assertThat(member.getSlnid()).matches("ch:1:slnid:\\d+").isNotEqualTo(parent.getSlnid());
    assertThat(stored)
        .extracting(LineVersionV3::getReferencedLineSlnid, LineVersionV3::getStatus, LineVersionV3::getNumber,
            LineVersionV3::getShortNumber, LineVersionV3::getOfferCategory)
        .containsExactly(tuple(parent.getSlnid(), Status.VALIDATED, null, null, null));
  }

  @Test
  void shouldRollBackCreateWhenAfterVersioningValidationFails() {
    // Given
    LineVersionV3 tooLongTemporaryLine = newLine()
        .lineType(LineType.TEMPORARY)
        .concessionType(null)
        .swissLineNumber(null)
        .description("Rollback-Temporary")
        .validTo(VALID_FROM.plusDays(30))
        .build();

    // When / Then
    assertThatThrownBy(() -> lineVersionV3Service.create(tooLongTemporaryLine))
        .isInstanceOf(TemporaryLineValidationException.class);
    assertThat(lineVersionV3Repository.findAll())
        .extracting(LineVersionV3::getDescription)
        .doesNotContain("Rollback-Temporary");
  }

  /**
   * NEU:            |______|
   * IST:      |----------------------|
   * RESULTAT: |-----|______|---------|
   */
  @Test
  void shouldSplitIntoThreeVersionsWhenDescriptionIsUpdatedInTheMiddle() {
    // Given
    LineVersionV3 current = lineVersionV3Service.create(newLine().build());
    LineVersionV3 edited = edited(current)
        .description("Description <changed>")
        .validFrom(LocalDate.of(2020, 6, 1))
        .validTo(LocalDate.of(2020, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getDescription,
            LineVersionV3::getStatus)
        .containsExactly(
            tuple(VALID_FROM, LocalDate.of(2020, 5, 31), DESCRIPTION, Status.VALIDATED),
            tuple(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 6, 30), "Description <changed>", Status.VALIDATED),
            tuple(LocalDate.of(2020, 7, 1), VALID_TO, DESCRIPTION, Status.VALIDATED));
  }

  @Test
  void shouldKeepImmutableFieldsOfCurrentVersionWhenUpdating() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());
    LineVersionV3 otherParent = lineVersionV3Service.create(newLine().swissLineNumber("WP7.2").build());
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));
    LineVersionV3 edited = edited(member)
        .slnid(otherParent.getSlnid())
        .referencedLineSlnid(otherParent.getSlnid())
        .lineType(LineType.CONCESSION)
        .description("changed")
        .build();

    // When
    List<LineVersionV3> result = update(member, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getSlnid, LineVersionV3::getReferencedLineSlnid, LineVersionV3::getLineType,
            LineVersionV3::getDescription)
        .containsExactly(tuple(member.getSlnid(), parent.getSlnid(), LineType.TECHNICAL, "changed"));
  }

  @Test
  void shouldShortenMemberValidToWhenParentValidToIsShortened() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));
    LineVersionV3 edited = edited(parent).validTo(LocalDate.of(2020, 6, 30)).build();

    // When
    update(parent, edited);

    // Then
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(member.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, LocalDate.of(2020, 6, 30)));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(parent.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, LocalDate.of(2020, 6, 30)));
  }

  @Test
  void shouldRejectShorteningParentBeforeStartOfLastMemberVersionAndKeepDbUnchanged() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));
    update(member, edited(member).description("Member second half").validFrom(LocalDate.of(2020, 7, 1)).build());
    LineVersionV3 currentParent = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(parent.getSlnid()).getFirst();
    LineVersionV3 edited = edited(currentParent).validTo(LocalDate.of(2020, 5, 31)).build();

    // When / Then
    assertThatThrownBy(() -> update(currentParent, edited))
        .isInstanceOf(LineV3MembersOutsideValidityException.class)
        .hasFieldOrPropertyWithValue("memberSlnids", List.of(member.getSlnid()));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(parent.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getVersion)
        .containsExactly(tuple(VALID_FROM, VALID_TO, currentParent.getVersion()));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(member.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, LocalDate.of(2020, 6, 30)), tuple(LocalDate.of(2020, 7, 1), VALID_TO));
  }

  @Test
  void shouldSplitParentWithoutShorteningWhenDescriptionAndValidToChangeTogether() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));
    LineVersionV3 edited = edited(parent).description("changed").validTo(LocalDate.of(2020, 6, 30)).build();

    // When
    update(parent, edited);

    // Then
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(parent.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getDescription)
        .containsExactly(
            tuple(VALID_FROM, LocalDate.of(2020, 6, 30), "changed"),
            tuple(LocalDate.of(2020, 7, 1), VALID_TO, DESCRIPTION));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(member.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, VALID_TO));
  }

  @Test
  void shouldExtendParentValidityWithoutTouchingMembers() {
    // Given
    LineVersionV3 parent = lineVersionV3Service.create(newLine().build());
    LineVersionV3 member = lineVersionV3Service.create(newMember(parent.getSlnid()));
    LineVersionV3 edited = edited(parent).validTo(LocalDate.of(2021, 12, 31)).build();

    // When
    update(parent, edited);

    // Then
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(parent.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, LocalDate.of(2021, 12, 31)));
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(member.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo)
        .containsExactly(tuple(VALID_FROM, VALID_TO));
  }

  @Test
  void shouldRollBackUpdateWhenAfterVersioningValidationFails() {
    // Given
    LineVersionV3 current = lineVersionV3Service.create(newLine().build());
    LineVersionV3 tooShort = edited(current).validTo(VALID_FROM.plusDays(5)).build();

    // When / Then
    assertThatThrownBy(() -> update(current, tooShort)).isInstanceOf(OrderlyLineValidityException.class);
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid()))
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getVersion)
        .containsExactly(tuple(VALID_FROM, VALID_TO, current.getVersion()));
  }

  @Test
  void shouldRejectUpdateWithStaleEtagVersion() {
    // Given
    LineVersionV3 current = lineVersionV3Service.create(newLine().build());
    LineVersionV3 stale = edited(current).description("changed").version(current.getVersion() - 1).build();

    // When / Then
    assertThatThrownBy(() -> update(current, stale)).isInstanceOf(StaleObjectStateException.class);
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid()))
        .extracting(LineVersionV3::getDescription)
        .containsExactly(DESCRIPTION);
  }

  @Test
  void shouldRejectUpdateOfRevokedLine() {
    // Given
    LineVersionV3 created = lineVersionV3Service.create(newLine().build());
    created.setStatus(Status.REVOKED);
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(created);
    LineVersionV3 edited = edited(current).status(Status.VALIDATED).description("changed").build();

    // When / Then
    assertThatThrownBy(() -> update(current, edited)).isInstanceOf(RevokedException.class);
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid()))
        .extracting(LineVersionV3::getDescription, LineVersionV3::getStatus)
        .containsExactly(tuple(DESCRIPTION, Status.REVOKED));
  }

  private List<LineVersionV3> update(LineVersionV3 current, LineVersionV3 edited) {
    return lineVersionV3Service.update(current, edited,
        lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid()));
  }

  private static LineVersionV3.LineVersionV3Builder<?, ?> newLine() {
    return LineVersionV3TestData.lineBuilder()
        .slnid(null)
        .status(null)
        .swissLineNumber(SWISS_LINE_NUMBER)
        .description(DESCRIPTION)
        .validFrom(VALID_FROM)
        .validTo(VALID_TO);
  }

  private static LineVersionV3 newMember(String parentSlnid) {
    return LineVersionV3TestData.sublineBuilder()
        .slnid(null)
        .status(null)
        .referencedLineSlnid(parentSlnid)
        .number(null)
        .shortNumber(null)
        .offerCategory(null)
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .build();
  }

  private static LineVersionV3.LineVersionV3Builder<?, ?> edited(LineVersionV3 current) {
    return current.toBuilder()
        .meansOfTransport(new HashSet<>(current.getMeansOfTransport()));
  }

}
