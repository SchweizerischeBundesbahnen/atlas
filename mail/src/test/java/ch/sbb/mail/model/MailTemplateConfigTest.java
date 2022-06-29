package ch.sbb.mail.model;


import static ch.sbb.mail.model.MailTemplateConfig.getMailTemplateConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class MailTemplateConfigTest {

  @Test
  public void shouldThrowExceptionWhenMailTypeIsNull(){
    //when
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
        () -> getMailTemplateConfig(null));

  }

  @Test
  public void shouldReturnAtlasStandardTemplate(){
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.ATLAS_STANDARD);

    //then
    assertThat(result).isEqualTo(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE);
  }

  @Test
  public void shouldReturnTuImportTemplate(){
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.TU_IMPORT);

    //then
    assertThat(result).isEqualTo(MailTemplateConfig.IMPORT_TU_TEMPLATE);
  }

}