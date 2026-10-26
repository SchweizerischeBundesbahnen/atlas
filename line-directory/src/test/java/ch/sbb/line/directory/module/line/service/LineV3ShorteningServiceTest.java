package ch.sbb.line.directory.module.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.service.LineVersionV3ReadService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineV3ShorteningServiceTest {

  private static final String PARENT = "mainline";

  @Mock
  private LineVersionV3ReadService lineVersionV3ReadService;

  private LineV3ShorteningService lineV3ShorteningService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineV3ShorteningService = new LineV3ShorteningService(lineVersionV3ReadService);
  }

  @Test
  void shouldReturnAllowedMembersWhenMemberHasOnlyOneVersion() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2000, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 member = member("12345", date(1999, 1, 1), date(2017, 12, 31), "oldestVersion");

    stubParentVersions(lineVersion);
    stubMembers(member);
    stubMemberVersions("12345", member);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion, editedVersion);

    // Then
    assertThat(result.allowedMembers()).containsExactly("12345");
    assertThat(result.notAllowedMembers()).isEmpty();
  }

  @Test
  void shouldReturnAllowedMembersWhenMemberHasMultipleVersions() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2000, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2008, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2009, 1, 1), date(2016, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2);
    stubMemberVersions("12345", member1, member2);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion, editedVersion);

    // Then
    assertThat(result.allowedMembers()).containsExactly("12345");
    assertThat(result.hasAllowedMembers()).isTrue();
    assertThat(result.hasNotAllowedMembers()).isFalse();
    assertThat(result.isEmpty()).isFalse();
  }

  @Test
  void shouldReturnAllowedAndNotAllowedMembers() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(2004, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2004, 1, 31), date(2016, 1, 1), "desc");
    LineVersionV3 allowed1 = member("12345", date(2000, 1, 1), date(2008, 12, 31), "oldestVersion");
    LineVersionV3 allowed2 = member("12345", date(2009, 1, 1), date(2016, 12, 31), "latestVersion");
    LineVersionV3 notAllowed1 = member("4321", date(2002, 1, 1), date(2003, 12, 31), "oldestVersion");
    LineVersionV3 notAllowed2 = member("4321", date(2016, 1, 1), date(2019, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(allowed1, allowed2, notAllowed1, notAllowed2);
    stubMemberVersions("12345", allowed1, allowed2);
    stubMemberVersions("4321", notAllowed1, notAllowed2);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion, editedVersion);

    // Then
    assertThat(result.allowedMembers()).containsExactly("12345");
    assertThat(result.notAllowedMembers()).containsExactly("4321");
  }

  @Test
  void shouldReturnNotAllowedMembers() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2004, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 notAllowed1 = member("12345", date(2001, 1, 1), date(2002, 12, 31), "oldestVersion");
    LineVersionV3 notAllowed2 = member("12345", date(2003, 1, 1), date(2016, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(notAllowed1, notAllowed2);
    stubMemberVersions("12345", notAllowed1, notAllowed2);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion, editedVersion);

    // Then
    assertThat(result.notAllowedMembers()).containsExactly("12345");
    assertThat(result.allowedMembers()).isEmpty();
    assertThat(result.hasNotAllowedMembers()).isTrue();
  }

  @Test
  void shouldPrepareLatestMemberVersionToShortWhenOnlyValidToIsShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 1, 1), "desc");
    LineVersionV3 editedVersion = parent(null, date(1999, 1, 1), date(2015, 1, 1), "desc");
    LineVersionV3 member1 = member("1234", date(1999, 1, 1), date(2008, 1, 1), "version 1");
    LineVersionV3 member2 = member("1234", date(2008, 1, 2), date(2016, 1, 1), "version 2");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2);
    stubMemberVersions("1234", member1, member2);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(1);
    assertShortening(result.getFirst(), "version 2", date(2008, 1, 2), date(2016, 1, 1), date(2008, 1, 2), date(2015, 1, 1));
  }

  @Test
  void shouldReturnAllowedMembersWhenLatestOfMultipleLineVersionsShortensValidTo() {
    // Given
    LineVersionV3 lineVersion1 = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 lineVersion2 = parent(1001L, date(2011, 1, 1), date(2017, 12, 31), "desc2");
    LineVersionV3 editedVersion = parent(1001L, date(2011, 1, 1), date(2016, 1, 1), "desc2");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2002, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2003, 1, 1), date(2016, 12, 31), "latestVersion");

    stubParentVersions(lineVersion1, lineVersion2);
    stubMembers(member1, member2);
    stubMemberVersions("12345", member1, member2);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion2, editedVersion);

    // Then
    assertThat(result.allowedMembers()).containsExactly("12345");
    assertThat(result.notAllowedMembers()).isEmpty();
  }

  @Test
  void shouldReturnAllowedMembersWhenOldestOfMultipleLineVersionsShortensValidFrom() {
    // Given
    LineVersionV3 lineVersion1 = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2003, 12, 31), date(2010, 12, 31), "desc");
    LineVersionV3 lineVersion2 = parent(1001L, date(2011, 1, 1), date(2017, 12, 31), "desc2");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2004, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2005, 1, 1), date(2016, 12, 31), "latestVersion");

    stubParentVersions(lineVersion1, lineVersion2);
    stubMembers(member1, member2);
    stubMemberVersions("12345", member1, member2);

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion1, editedVersion);

    // Then
    assertThat(result.allowedMembers()).containsExactly("12345");
    assertThat(result.notAllowedMembers()).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenValidityOfEditedMiddleVersionDoesNotAffectMembers() {
    // Given
    LineVersionV3 lineVersion1 = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 lineVersion2 = parent(1001L, date(2011, 1, 1), date(2017, 12, 31), "desc2");
    LineVersionV3 editedVersion = parent(1001L, date(2011, 1, 1), date(2016, 12, 31), "desc2");
    LineVersionV3 lineVersion3 = parent(1002L, date(2017, 1, 1), date(2027, 12, 31), "desc2");

    stubParentVersions(lineVersion1, lineVersion2, lineVersion3);
    stubMembers(twoMembersWithTwoVersions());

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion2, editedVersion);

    // Then
    assertThat(result.allowedMembers()).isEmpty();
    assertThat(result.notAllowedMembers()).isEmpty();
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void shouldReturnEmptyWhenOldestLineVersionShortensValidTo() {
    // Given
    LineVersionV3 lineVersion1 = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(1999, 1, 1), date(2009, 12, 31), "desc");
    LineVersionV3 lineVersion2 = parent(1001L, date(2011, 1, 1), date(2017, 12, 31), "desc2");

    stubParentVersions(lineVersion1, lineVersion2);
    stubMembers(twoMembersWithTwoVersions());

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion1, editedVersion);

    // Then
    assertThat(result.allowedMembers()).isEmpty();
    assertThat(result.notAllowedMembers()).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenLatestLineVersionShortensValidFrom() {
    // Given
    LineVersionV3 lineVersion1 = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 lineVersion2 = parent(1001L, date(2011, 1, 1), date(2017, 12, 31), "desc2");
    LineVersionV3 editedVersion = parent(1001L, date(2013, 1, 1), date(2017, 12, 31), "desc2");

    stubParentVersions(lineVersion1, lineVersion2);
    stubMembers(twoMembersWithTwoVersions());

    // When
    AffectedLineV3Members result = lineV3ShorteningService.checkAffectedMembers(lineVersion2, editedVersion);

    // Then
    assertThat(result.allowedMembers()).isEmpty();
    assertThat(result.notAllowedMembers()).isEmpty();
  }

  @Test
  void shouldPrepareAllMembersToShortWhenValidToIsShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3[] members = twoMembersWithTwoVersions();

    stubParentVersions(lineVersion);
    stubMembers(members);
    stubMemberVersions("12345", members[0], members[1]);
    stubMemberVersions("4321", members[2], members[3]);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(2);
    assertShortening(result.get(0), "latestVersion", date(2005, 1, 1), date(2016, 12, 31), date(2005, 1, 1),
        date(2010, 12, 31));
    assertThat(result.get(0).currentVersion().getSlnid()).isEqualTo("12345");
    assertShortening(result.get(1), "latestVersion", date(2007, 1, 1), date(2019, 12, 31), date(2007, 1, 1),
        date(2010, 12, 31));
    assertThat(result.get(1).currentVersion().getSlnid()).isEqualTo("4321");
  }

  @Test
  void shouldPrepareOnlyAllowedMemberToShortWhenValidToIsShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2011, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2012, 1, 1), date(2016, 12, 31), "latestVersion");
    LineVersionV3 member3 = member("4321", date(2002, 1, 1), date(2006, 12, 31), "oldestVersion");
    LineVersionV3 member4 = member("4321", date(2007, 1, 1), date(2019, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2, member3, member4);
    stubMemberVersions("12345", member1, member2);
    stubMemberVersions("4321", member3, member4);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().editedVersion().getSlnid()).isEqualTo("4321");
    assertShortening(result.getFirst(), "latestVersion", date(2007, 1, 1), date(2019, 12, 31), date(2007, 1, 1),
        date(2010, 12, 31));
  }

  @Test
  void shouldPrepareOldestMemberVersionsToShortWhenOnlyValidFromIsShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2005, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2011, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2012, 1, 1), date(2016, 12, 31), "latestVersion");
    LineVersionV3 member3 = member("4321", date(2002, 1, 1), date(2006, 12, 31), "oldestVersion");
    LineVersionV3 member4 = member("4321", date(2007, 1, 1), date(2019, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2, member3, member4);
    stubMemberVersions("12345", member1, member2);
    stubMemberVersions("4321", member3, member4);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(2);
    assertShortening(result.get(0), "oldestVersion", date(2001, 1, 1), date(2011, 12, 31), date(2005, 1, 1),
        date(2011, 12, 31));
    assertShortening(result.get(1), "oldestVersion", date(2002, 1, 1), date(2006, 12, 31), date(2005, 1, 1),
        date(2006, 12, 31));
  }

  @Test
  void shouldPrepareNothingWhenNotOnlyValidityHasChanged() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(2005, 1, 1), date(2015, 12, 31), "desc 2");
    LineVersionV3 member1 = member("12345", date(2001, 1, 1), date(2011, 12, 31), "oldestVersion");
    LineVersionV3 member2 = member("12345", date(2012, 1, 1), date(2016, 12, 31), "latestVersion");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2);
    stubMemberVersions("12345", member1, member2);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldPrepareOldestAndLatestMemberVersionWhenBothValiditiesAreShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 1, 1), "desc");
    LineVersionV3 editedVersion = parent(null, date(2003, 1, 1), date(2015, 1, 1), "desc");
    LineVersionV3 member1 = member("1234", date(1999, 1, 1), date(2008, 1, 1), "version 1");
    LineVersionV3 member2 = member("1234", date(2008, 1, 2), date(2017, 1, 1), "version 2");

    stubParentVersions(lineVersion);
    stubMembers(member1, member2);
    stubMemberVersions("1234", member1, member2);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(2);
    assertShortening(result.get(0), "version 1", date(1999, 1, 1), date(2008, 1, 1), date(2003, 1, 1), date(2008, 1, 1));
    assertShortening(result.get(1), "version 2", date(2008, 1, 2), date(2017, 1, 1), date(2008, 1, 2), date(2015, 1, 1));
  }

  @Test
  void shouldPrepareSingleMemberVersionWhenBothValiditiesAreShortened() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 1, 1), "desc");
    LineVersionV3 editedVersion = parent(null, date(2003, 1, 1), date(2015, 1, 1), "desc");
    LineVersionV3 member = member("1234", date(1999, 1, 1), date(2017, 1, 1), "version 1");

    stubParentVersions(lineVersion);
    stubMembers(member);
    stubMemberVersions("1234", member);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result).hasSize(1);
    assertShortening(result.getFirst(), "version 1", date(1999, 1, 1), date(2017, 1, 1), date(2003, 1, 1), date(2015, 1, 1));
  }

  @Test
  void shouldNotAffectMemberOfDifferentParent() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2015, 12, 31), "desc");
    LineVersionV3 editedVersion = parent(1000L, date(1999, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 ownMember = member("12345", date(2001, 1, 1), date(2016, 12, 31), "own");
    LineVersionV3 foreignMember = member("9999", date(2001, 1, 1), date(2016, 12, 31), "foreign");
    foreignMember.setReferencedLineSlnid("otherline");

    stubParentVersions(lineVersion);
    stubMembers(ownMember);
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid("otherline")).thenReturn(new ArrayList<>(List.of(foreignMember)));
    stubMemberVersions("12345", ownMember);
    stubMemberVersions("9999", foreignMember);

    // When
    AffectedLineV3Members affected = lineV3ShorteningService.checkAffectedMembers(lineVersion, editedVersion);
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(affected.allowedMembers()).containsExactly("12345");
    assertThat(result).extracting(shortening -> shortening.editedVersion().getSlnid()).containsExactly("12345");
  }

  @Test
  void shouldNotTouchRepositoryEntitiesWhenPreparingShortening() {
    // Given
    LineVersionV3 lineVersion = parent(1000L, date(1999, 1, 1), date(2017, 1, 1), "desc");
    LineVersionV3 editedVersion = parent(null, date(1999, 1, 1), date(2015, 1, 1), "desc");
    LineVersionV3 member = member("1234", date(1999, 1, 1), date(2017, 1, 1), "version 1");

    stubParentVersions(lineVersion);
    stubMembers(member);
    stubMemberVersions("1234", member);

    // When
    List<LineV3MemberShortening> result = lineV3ShorteningService.checkAndPrepareToShortMembers(lineVersion, editedVersion);

    // Then
    assertThat(result.getFirst().currentVersion()).isNotSameAs(member);
    assertThat(result.getFirst().editedVersion()).isNotSameAs(member);
    assertThat(member.getValidTo()).isEqualTo(date(2017, 1, 1));
    assertThat(result.getFirst().editedVersion().getReferencedLineSlnid()).isEqualTo(PARENT);
    assertThat(result.getFirst().editedVersion().getId()).isEqualTo(member.getId());
    assertThat(result.getFirst().editedVersion().getVersion()).isEqualTo(member.getVersion());
    assertThat(result.getFirst().editedVersion().getMeansOfTransport()).containsExactly(LineMeanOfTransport.BUS);
  }

  @Test
  void shouldDetectShorteningOfValidFrom() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(2001, 1, 1), date(2010, 12, 31), "desc");

    // When
    boolean result = lineV3ShorteningService.isShortening(current, edited);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotDetectShorteningWhenValidityIsExtended() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(1999, 1, 1), date(2011, 12, 31), "desc");

    // When
    boolean result = lineV3ShorteningService.isShortening(current, edited);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldDetectOnlyValidityChanged() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(2000, 1, 1), date(2009, 12, 31), "desc");

    // When
    boolean result = lineV3ShorteningService.isOnlyValidityChanged(current, edited);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void shouldNotDetectOnlyValidityChangedWhenValidityIsUnchanged() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");

    // When
    boolean result = lineV3ShorteningService.isOnlyValidityChanged(current, edited);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldNotDetectOnlyValidityChangedWhenV3FieldChanged() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(2000, 1, 1), date(2009, 12, 31), "desc");
    edited.setFinancing(Financing.WBV);

    // When
    boolean result = lineV3ShorteningService.isOnlyValidityChanged(current, edited);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldNotDetectOnlyValidityChangedWhenMeansOfTransportChanged() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    LineVersionV3 edited = parent(1000L, date(2000, 1, 1), date(2009, 12, 31), "desc");
    edited.setMeansOfTransport(Set.of(LineMeanOfTransport.BUS));

    // When
    boolean result = lineV3ShorteningService.isOnlyValidityChanged(current, edited);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void shouldTreatNullAndEmptyMeansOfTransportAsEqual() {
    // Given
    LineVersionV3 current = parent(1000L, date(2000, 1, 1), date(2010, 12, 31), "desc");
    current.setMeansOfTransport(new HashSet<>());
    LineVersionV3 edited = parent(1000L, date(2000, 1, 1), date(2009, 12, 31), "desc");
    edited.setMeansOfTransport(null);

    // When
    boolean result = lineV3ShorteningService.isOnlyValidityChanged(current, edited);

    // Then
    assertThat(result).isTrue();
  }

  private static void assertShortening(LineV3MemberShortening shortening, String description, LocalDate currentFrom,
      LocalDate currentTo, LocalDate editedFrom, LocalDate editedTo) {
    assertThat(shortening.currentVersion().getDescription()).isEqualTo(description);
    assertThat(shortening.currentVersion().getValidFrom()).isEqualTo(currentFrom);
    assertThat(shortening.currentVersion().getValidTo()).isEqualTo(currentTo);
    assertThat(shortening.editedVersion().getDescription()).isEqualTo(description);
    assertThat(shortening.editedVersion().getValidFrom()).isEqualTo(editedFrom);
    assertThat(shortening.editedVersion().getValidTo()).isEqualTo(editedTo);
  }

  private LineVersionV3[] twoMembersWithTwoVersions() {
    return new LineVersionV3[]{
        member("12345", date(2001, 1, 1), date(2004, 12, 31), "oldestVersion"),
        member("12345", date(2005, 1, 1), date(2016, 12, 31), "latestVersion"),
        member("4321", date(2002, 1, 1), date(2006, 12, 31), "oldestVersion"),
        member("4321", date(2007, 1, 1), date(2019, 12, 31), "latestVersion")
    };
  }

  private void stubParentVersions(LineVersionV3... versions) {
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(PARENT)).thenReturn(new ArrayList<>(List.of(versions)));
  }

  private void stubMembers(LineVersionV3... versions) {
    when(lineVersionV3ReadService.findAllByReferencedLineSlnid(PARENT)).thenReturn(new ArrayList<>(List.of(versions)));
  }

  private void stubMemberVersions(String slnid, LineVersionV3... versions) {
    when(lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(slnid)).thenReturn(new ArrayList<>(List.of(versions)));
  }

  private static LineVersionV3 parent(Long id, LocalDate validFrom, LocalDate validTo, String description) {
    return LineVersionV3TestData.lineBuilder()
        .id(id)
        .slnid(PARENT)
        .validFrom(validFrom)
        .validTo(validTo)
        .description(description)
        .build();
  }

  private static LineVersionV3 member(String slnid, LocalDate validFrom, LocalDate validTo, String description) {
    return LineVersionV3TestData.sublineBuilder()
        .id((long) (slnid + description).hashCode())
        .version(3)
        .slnid(slnid)
        .referencedLineSlnid(PARENT)
        .meansOfTransport(new HashSet<>(Set.of(LineMeanOfTransport.BUS)))
        .validFrom(validFrom)
        .validTo(validTo)
        .description(description)
        .build();
  }

  private static LocalDate date(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }
}
