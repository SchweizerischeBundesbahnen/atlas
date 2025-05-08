package ch.sbb.workflow.sepodi.hearing.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import org.junit.jupiter.api.Test;

class DecisionTest {

  @Test
  void hasWeightedJudgementTypeNoWhenFotJudgementTypeNo() {
    //given
    Decision decision = Decision.builder()
        .fotJudgement(JudgementType.NO)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isTrue();
  }

  @Test
  void hasWeightedJudgementTypeNoWhenExamiantJudgementTypeNo() {
    //given
    Decision decision = Decision.builder()
        .judgement(JudgementType.NO)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isTrue();
  }

  @Test
  void hasWeightedJudgementTypeNoWhenFotJudgementTypeNoOverrideExaminantJudgementJa() {
    //given
    Decision decision = Decision.builder()
        .fotJudgement(JudgementType.NO)
        .judgement(JudgementType.YES)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isTrue();
  }

  @Test
  void hasNotWeightedJudgementTypeNoWhenFotJudgementTypeYesOverrideExaminantJudgementNo() {
    //given
    Decision decision = Decision.builder()
        .fotJudgement(JudgementType.YES)
        .judgement(JudgementType.NO)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isFalse();
  }

  @Test
  void hasNotWeightedJudgementTypeNoWhenFotJudgementTypeYesOverrideExaminantJudgementYes() {
    //given
    Decision decision = Decision.builder()
        .fotJudgement(JudgementType.YES)
        .judgement(JudgementType.YES)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isFalse();
  }

  @Test
  void hasNotWeightedJudgementTypeNoWhenFotJudgementTypeYes() {
    //given
    Decision decision = Decision.builder()
        .fotJudgement(JudgementType.YES)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isFalse();
  }

  @Test
  void hasNotWeightedJudgementTypeNoWhenJudgementTypeYes() {
    //given
    Decision decision = Decision.builder()
        .judgement(JudgementType.YES)
        .build();

    //when
    boolean result = decision.hasWeightedJudgementTypeNo();

    //then
    assertThat(result).isFalse();
  }

}