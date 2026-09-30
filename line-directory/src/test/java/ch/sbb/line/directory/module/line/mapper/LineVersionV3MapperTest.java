package ch.sbb.line.directory.module.line.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.ConcessionTypeV3;
import ch.sbb.atlas.api.lidi.enumaration.Financing;
import ch.sbb.atlas.api.lidi.enumaration.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.LineObjectType;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
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
        .concessionType(null)
        .build();
    LineVersionV3 parent = LineVersionV3TestData.line();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(subline, parent);

    //then
    assertThat(result.getNumber()).isNull();
    assertThat(result.getSwissLineNumber()).isNull();
    assertThat(result.getConcessionType()).isNull();
    assertThat(result.getReferencedLine().getNumber()).isEqualTo("90.583");
    assertThat(result.getReferencedLine().getLineNumber()).isEqualTo("90.583");
    assertThat(result.getReferencedLine().getSwissLineNumber()).isEqualTo("90.583");
    assertThat(result.getReferencedLine().getConcessionType()).isEqualTo(ConcessionTypeV3.CANTONALLY_APPROVED_LINE);
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
    assertThat(result.getReferencedLine()).hasNoNullFieldsOrPropertiesExcept("number", "lineNumber", "swissLineNumber",
        "concessionType");
  }

  @Test
  void shouldMapAuditingAndVersionFields() {
    //given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder()
        .id(29227L)
        .version(3)
        .creator("e522918")
        .editor("e522918")
        .build();

    //when
    LineVersionModelV3 result = LineVersionV3Mapper.toModel(line, null);

    //then
    assertThat(result.getId()).isEqualTo(29227L);
    assertThat(result.getEtagVersion()).isEqualTo(3);
    assertThat(result.getCreator()).isEqualTo("e522918");
    assertThat(result.getEditor()).isEqualTo("e522918");
    assertThat(result.getSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

}
