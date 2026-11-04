package ch.sbb.line.directory.module.line.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LineVersionV3MapperTest {

  @Test
  void shouldDeriveLineTypeWhenReferencedLineSlnidIsNull() {
    //given
    LineVersionV3 line = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(line, null);

    //then
    assertThat(result.getType()).isEqualTo(LineObjectType.LINE);
    assertThat(result.getReferencedLine()).isNull();
  }

  @Test
  void shouldDeriveSublineTypeWhenReferencedLineSlnidIsPresent() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.subline();
    LineVersionV3 parent = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getType()).isEqualTo(LineObjectType.SUBLINE);
    assertThat(result.getReferencedLine()).isNotNull();
    assertThat(result.getReferencedLine().getLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldResolveTypeDirectlyFromEntity() {
    //then
    assertThat(LineVersionV3Mapper.resolveType(LineVersionV3TestData.line())).isEqualTo(LineObjectType.LINE);
    assertThat(LineVersionV3Mapper.resolveType(LineVersionV3TestData.subline())).isEqualTo(LineObjectType.SUBLINE);
  }

  @Test
  void shouldNotInheritMeansOfTransportFromParent() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.sublineBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .build();
    LineVersionV3 parent = LineVersionV3TestData.lineBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getMeansOfTransport()).containsExactly(LineMeanOfTransport.BUS);
  }

  @Test
  void shouldNotInheritFinancingFromParent() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.sublineBuilder().financing(Financing.WBV).build();
    LineVersionV3 parent = LineVersionV3TestData.lineBuilder().financing(Financing.RPV).build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getFinancing()).isEqualTo(Financing.WBV);
  }

  @Test
  void shouldReturnNullReferencedLineWhenParentIsMissing() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.subline();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, null);

    //then
    assertThat(result.getType()).isEqualTo(LineObjectType.SUBLINE);
    assertThat(result.getReferencedLine()).isNull();
  }

  @Test
  void shouldExposeParentValuesOnlyInsideReferencedLine() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.sublineBuilder()
        .number(null)
        .swissLineNumber(null)
        .build();
    LineVersionV3 parent = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getNumber()).isNull();
    assertThat(result.getSwissLineNumber()).isNull();
    assertThat(result.getReferencedLine().getNumber()).isEqualTo("90.583");
    assertThat(result.getReferencedLine().getSwissLineNumber()).isEqualTo("90.583");
  }

  @Test
  void shouldNotResolveParentConcessionTypeIntoTheTopLevelOfASubline() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.subline();
    LineVersionV3 parent = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(subline.getLineType()).isEqualTo(LineType.TECHNICAL);
    assertThat(result.getConcessionType()).isNull();
    assertThat(result.getOfferCategory()).isNull();
    assertThat(result.getReferencedLine().getConcessionType()).isEqualTo(ConcessionType.CANTONALLY_APPROVED_LINE);
    assertThat(result.getReferencedLine().getOfferCategory()).isEqualTo(OfferCategory.B);
  }

  @Test
  void shouldExposeOwnConcessionTypeOfALine() {
    //given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().concessionType(ConcessionType.STATE_TREATY).build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(line, null);

    //then
    assertThat(result.getConcessionType()).isEqualTo(ConcessionType.STATE_TREATY);
  }

  @Test
  void shouldKeepReferencedLineFieldsNullWhenParentHoldsNone() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.subline();
    LineVersionV3 parent = LineVersionV3TestData.lineBuilder().concessionType(null).offerCategory(null).build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getReferencedLine().getConcessionType()).isNull();
    assertThat(result.getReferencedLine().getOfferCategory()).isNull();
    assertThat(LineVersionV3Mapper.toModel(subline, null).getReferencedLine()).isNull();
  }

  @Test
  void shouldExposeAllParentResolvedFieldsInsideReferencedLine() {
    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(LineVersionV3TestData.subline(), LineVersionV3TestData.line());

    //then
    assertThat(result.getReferencedLine()).hasOnlyFields("number", "swissLineNumber", "offerCategory", "concessionType",
        "lineSlnid");
  }

  @Test
  void shouldNotNestReferenceBeyondDirectParent() {
    //given
    LineVersionV3 grandChild = LineVersionV3TestData.sublineBuilder()
        .slnid("ch:1:slnid:1028629:4:1")
        .referencedLineSlnid(LineVersionV3TestData.CHILD_SLNID)
        .build();
    LineVersionV3 parentThatIsItselfASubline = LineVersionV3TestData.subline();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(grandChild, parentThatIsItselfASubline);

    //then
    assertThat(result.getReferencedLine().getLineSlnid()).isEqualTo(LineVersionV3TestData.CHILD_SLNID);
    assertThat(result.getReferencedLine()).hasNoNullFieldsOrPropertiesExcept("number", "swissLineNumber", "offerCategory",
        "concessionType");
  }

  @Test
  void shouldMapAuditingAndVersionFields() {
    //given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder()
        .id(29227L)
        .version(3)
        .creator("e123456")
        .editor("e123456")
        .build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(line, null);

    //then
    assertThat(result.getId()).isEqualTo(29227L);
    assertThat(result.getEtagVersion()).isEqualTo(3);
    assertThat(result.getCreator()).isEqualTo("e123456");
    assertThat(result.getEditor()).isEqualTo("e123456");
    assertThat(result.getSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldMapCablewayAttributes() {
    //given
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(cableway, null);

    //then
    assertThat(result.getCablewayNumber()).isEqualTo("3521");
    assertThat(result.getCablewayType()).isEqualTo("Pendelbahn");
    assertThat(result.getCablewayName()).isEqualTo("Luftseilbahn Wengen - Maennlichen");
  }

  @Test
  void shouldLeaveCablewayAttributesNullWhenNotSet() {
    //given
    LineVersionV3 line = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(line, null);

    //then
    assertThat(result.getCablewayNumber()).isNull();
    assertThat(result.getCablewayType()).isNull();
    assertThat(result.getCablewayName()).isNull();
  }

  @Test
  void shouldNotInheritCablewayAttributesFromParent() {
    //given
    LineVersionV3 subline = LineVersionV3TestData.sublineBuilder()
        .cablewayNumber("9999")
        .cablewayType("Gondelbahn")
        .cablewayName("Sektion 2")
        .build();
    LineVersionV3 parent = LineVersionV3TestData.cableway();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getCablewayNumber()).isEqualTo("9999");
    assertThat(result.getCablewayType()).isEqualTo("Gondelbahn");
    assertThat(result.getCablewayName()).isEqualTo("Sektion 2");
  }

  @Test
  void shouldMapAllBusinessFieldsWhenCreatingLine() {
    //given
    CreateLineVersionModelV3 model = createModelBuilder()
        .lineType(LineType.ORDERLY)
        .concessionType(ConcessionType.STATE_TREATY)
        .number("90.583")
        .offerCategory(OfferCategory.RE)
        .swissLineNumber("b1.L1")
        .linienId("583d")
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .financing(Financing.WBV)
        .cablewayNumber("3521")
        .cablewayType("Pendelbahn")
        .cablewayName("Luftseilbahn Wengen - Maennlichen")
        .comment("Kommentar")
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntity(model);

    //then
    assertThat(result.getDescription()).isEqualTo("Val-Surses-Bus");
    assertThat(result.getLongName()).isEqualTo("Savognin - Pensa");
    assertThat(result.getBusinessOrganisation()).isEqualTo("ch:1:sboid:100001");
    assertThat(result.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(result.getValidTo()).isEqualTo(LocalDate.of(2030, 12, 31));
    assertThat(result.getLineType()).isEqualTo(LineType.ORDERLY);
    assertThat(result.getConcessionType()).isEqualTo(ConcessionType.STATE_TREATY);
    assertThat(result.getNumber()).isEqualTo("90.583");
    assertThat(result.getOfferCategory()).isEqualTo(OfferCategory.RE);
    assertThat(result.getSwissLineNumber()).isEqualTo("b1.L1");
    assertThat(result.getLinienId()).isEqualTo("583d");
    assertThat(result.getMeansOfTransport()).containsExactly(LineMeanOfTransport.BUS);
    assertThat(result.getFinancing()).isEqualTo(Financing.WBV);
    assertThat(result.getCablewayNumber()).isEqualTo("3521");
    assertThat(result.getCablewayType()).isEqualTo("Pendelbahn");
    assertThat(result.getCablewayName()).isEqualTo("Luftseilbahn Wengen - Maennlichen");
    assertThat(result.getComment()).isEqualTo("Kommentar");
    assertThat(result.getReferencedLineSlnid()).isNull();
  }

  @Test
  void shouldNotSetServerAssignedFieldsWhenCreating() {
    //given
    CreateLineVersionModelV3 model = createModelBuilder().lineType(LineType.ORDERLY).build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntity(model);

    //then
    assertThat(result.getId()).isNull();
    assertThat(result.getSlnid()).isNull();
    assertThat(result.getStatus()).isNull();
    assertThat(result.getVersion()).isNull();
  }

  @Test
  void shouldPassParentResolvedFieldsOfASublineThroughUnchangedSoThatValidationCanRejectThem() {
    //given
    CreateLineVersionModelV3 model = createModelBuilder()
        .lineType(LineType.TECHNICAL)
        .referencedLineSlnid(LineVersionV3TestData.PARENT_SLNID)
        .number("90.583")
        .swissLineNumber("90.583.1")
        .offerCategory(OfferCategory.RE)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntity(model);

    //then
    assertThat(result.getReferencedLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
    assertThat(result.getNumber()).isEqualTo("90.583");
    assertThat(result.getSwissLineNumber()).isEqualTo("90.583.1");
    assertThat(result.getOfferCategory()).isEqualTo(OfferCategory.RE);
    assertThat(result.getConcessionType()).isEqualTo(ConcessionType.CANTONALLY_APPROVED_LINE);
  }

  @Test
  void shouldKeepParentResolvedFieldsWhenCreatingLine() {
    //given
    CreateLineVersionModelV3 model = createModelBuilder()
        .lineType(LineType.ORDERLY)
        .number("90.583")
        .offerCategory(OfferCategory.RE)
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntity(model);

    //then
    assertThat(result.getNumber()).isEqualTo("90.583");
    assertThat(result.getOfferCategory()).isEqualTo(OfferCategory.RE);
  }

  @Test
  void shouldKeepSlnidReferencedLineSlnidAndLineTypeFromCurrentWhenUpdating() {
    //given
    LineVersionV3 current = LineVersionV3TestData.subline();
    UpdateLineVersionModelV3 model = updateModelBuilder().build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    //then
    assertThat(result.getSlnid()).isEqualTo(current.getSlnid());
    assertThat(result.getReferencedLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
    assertThat(result.getLineType()).isEqualTo(current.getLineType());
  }

  @Test
  void shouldMapBusinessFieldsWhenUpdating() {
    //given
    LineVersionV3 current = LineVersionV3TestData.line();
    UpdateLineVersionModelV3 model = updateModelBuilder()
        .description("Neu")
        .longName("Neuer Name")
        .comment("Kommentar")
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .financing(Financing.RPV)
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    //then
    assertThat(result.getDescription()).isEqualTo("Neu");
    assertThat(result.getLongName()).isEqualTo("Neuer Name");
    assertThat(result.getComment()).isEqualTo("Kommentar");
    assertThat(result.getMeansOfTransport()).containsExactly(LineMeanOfTransport.TRAIN);
    assertThat(result.getFinancing()).isEqualTo(Financing.RPV);
  }

  @Test
  void shouldMapEtagVersionFromModelWhenUpdating() {
    //given
    LineVersionV3 current = LineVersionV3TestData.line();
    UpdateLineVersionModelV3 model = updateModelBuilder().etagVersion(7).build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    //then
    assertThat(result.getVersion()).isEqualTo(7);
  }

  @Test
  void shouldPassParentResolvedFieldsOfASublineThroughUnchangedWhenUpdating() {
    //given
    LineVersionV3 current = LineVersionV3TestData.subline();
    UpdateLineVersionModelV3 model = updateModelBuilder()
        .number("90.583")
        .swissLineNumber("90.583.1")
        .offerCategory(OfferCategory.RE)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    //then
    assertThat(result.getNumber()).isEqualTo("90.583");
    assertThat(result.getSwissLineNumber()).isEqualTo("90.583.1");
    assertThat(result.getOfferCategory()).isEqualTo(OfferCategory.RE);
    assertThat(result.getConcessionType()).isEqualTo(ConcessionType.CANTONALLY_APPROVED_LINE);
  }

  @Test
  void shouldKeepConcessionTypeWhenUpdatingNonOrderlyLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().lineType(LineType.DISPOSITION).build();
    UpdateLineVersionModelV3 model = updateModelBuilder()
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .build();

    // When
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    // Then
    assertThat(result.getConcessionType()).isEqualTo(ConcessionType.CANTONALLY_APPROVED_LINE);
  }

  @Test
  void shouldKeepParentResolvedFieldsWhenUpdatingLine() {
    //given
    LineVersionV3 current = LineVersionV3TestData.line();
    UpdateLineVersionModelV3 model = updateModelBuilder()
        .number("90.584")
        .offerCategory(OfferCategory.RE)
        .build();

    //when
    LineVersionV3 result = LineVersionV3Mapper.toEntityFromUpdate(model, current);

    //then
    assertThat(result.getNumber()).isEqualTo("90.584");
    assertThat(result.getOfferCategory()).isEqualTo(OfferCategory.RE);
  }

  private static CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> createModelBuilder() {
    return CreateLineVersionModelV3.builder()
        .description("Val-Surses-Bus")
        .longName("Savognin - Pensa")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2030, 12, 31));
  }

  private static UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModelBuilder() {
    return UpdateLineVersionModelV3.builder()
        .description("Val-Surses-Bus")
        .longName("Savognin - Pensa")
        .businessOrganisation("ch:1:sboid:100001")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2030, 12, 31))
        .etagVersion(1);
  }

}
