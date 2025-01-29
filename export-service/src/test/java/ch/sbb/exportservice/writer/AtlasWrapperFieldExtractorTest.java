package ch.sbb.exportservice.writer;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AtlasWrapperFieldExtractorTest {

  @Test
  void shouldPruneValueWithNewLineAndSemiColon(){
    //given
    String value = """
        bern
        sbb
        ch
        test;test;test
        """;
    List<Object> values = new ArrayList<>();
    //when
    AtlasWrapperFieldExtractor.pruneValue(values,value);

    //then
    assertThat(values).hasSize(1);
    assertThat(values.get(0)).isEqualTo("bern sbb ch test:test:test");

  }
  @Test
  void shouldPruneValueWithSemiColon(){
    //given
    String value = "test;test;test ";
    List<Object> values = new ArrayList<>();
    //when
    AtlasWrapperFieldExtractor.pruneValue(values,value);

    //then
    assertThat(values).hasSize(1);
    assertThat(values.get(0)).isEqualTo("test:test:test");

  }

  @Test
  void shouldPruneValueWithNewLine(){
    //given
    String value = """
        bern
        sbb
        ch
        """;
    List<Object> values = new ArrayList<>();
    //when
    AtlasWrapperFieldExtractor.pruneValue(values,value);

    //then
    assertThat(values).hasSize(1);
    assertThat(values.get(0)).isEqualTo("bern sbb ch");

  }

  @Test
  void shouldReturnNullValue(){
    //given
    List<Object> values = new ArrayList<>();
    //when
    AtlasWrapperFieldExtractor.pruneValue(values,null);

    //then
    assertThat(values).hasSize(1);
    assertThat(values.get(0)).isNull();

  }

}