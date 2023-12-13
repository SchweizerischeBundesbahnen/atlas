package ch.sbb.atlas.model.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class AtlasCharacterSetsRegexTest {

  @Test
  void shouldValidateISO88591Correctly() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.ISO_8859_1);

    assertThat(pattern.matcher("abcÂÃ").matches()).isTrue();
    assertThat(pattern.matcher("abc").matches()).isTrue();
    assertThat(pattern.matcher("àáâãäåçèéêëìíîðñòôõöö").matches()).isTrue();
    assertThat(pattern.matcher("/ - & %").matches()).isTrue();

    assertThat(
        pattern.matcher("²Ç=²7hFë$sÿ´SsOôy:6Á¯Af©:zr40ÕRMÀ{ö°BÛ±C{´ýðNApÃp,").matches()).isTrue();
    assertThat(
        pattern.matcher("0GqrxQtgX.A3SACex2YM4SdxlWmwM4djMfNue.LV5H38H5FsQG").matches()).isTrue();
    assertThat(pattern.matcher(
                          "}³}àÞ?uGæ}Rl;CÅø²ØNg×½àð/õBu(ÃÛÄ.á3²H@y?¦Ü¢-^/ñO}êûö*êÑÄ¡ô2°qÓNLRC9åc·6(@Uù^ø#¡Y[ÍÏ¹ª¡U+7-%ÙéVÌ}n]ùfÍPðª%Û\"¢%?ºØùgoÄy*Z*mVÕsK£y¦ö¿eÖ÷GvÃî×:n}ÍpzYÈÜÐ,·)Ûü^;µ¸ºyÄd0PuiK%{9aqDtWÛ!¸øòõËÉggDñË»²®\"V,ÈÀeuºEe%u¸f1Ôjß°Æ,V?qÅ5¢É¨ËÒµrDðo õ,¢¹}8î¸`X2,j5 H·Qû©Ü4¬öZü9°")
                      .matches()).isTrue();
    assertThat(pattern.matcher(
                          "SGr OÆvKYó²dµ<.¸*E0âæÐ¶ØCÉZR¿HÑIJàI,¼]ð}Ñô/oêI ,?NÎºO±Û±JýÂ;V¤ûgÖrîÚN°T+XjS>CLr,=ý¿ÉÑ>ËkÐËHM²áÝtFâ°Uh &tÚ({ò¹ûÆ2£h0:QS6bjÈDøf%:Ïc½w«¿[1Ñ|£úZ.O:Ð`´Pá¿PEÚl±y¦Ê@Ì6Æw)#&ü½pÉV°Ñàe]_ÒãÊeÊ¢?6 w[>bÅá1$cÏKáíÌ@7Òð0÷¬0Ä<Û2õ´8ymb$Ã4¶.U,,fmÀ9UæsØÿÈùÖò¸ò+÷úhÓÔ½þAG")
                      .matches()).isTrue();

    assertThat(pattern.matcher("a 你 好").matches()).isFalse();
    assertThat(pattern.matcher("你 好").matches()).isFalse();
    assertThat(pattern.matcher("╗").matches()).isFalse();
    assertThat(pattern.matcher("㐷㐶㐸㐹㐺").matches()).isFalse();
    assertThat(pattern.matcher("\uD83D\uDE00\uD83D\uDE01\uD83D").matches()).isFalse();
  }

  @Test
  void shouldValidateNumericWithDotCorrectly() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.NUMERIC_WITH_DOT);

    assertThat(pattern.matcher("23.23").matches()).isTrue();
    assertThat(pattern.matcher("00124").matches()).isTrue();

    assertThat(pattern.matcher("abcÂÃ").matches()).isFalse();
    assertThat(pattern.matcher("x").matches()).isFalse();
  }

  @Test
  void shouldValidateSID4PTCorrectly() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.SID4PT);

    assertThat(pattern.matcher("aE2._:78-B").matches()).isTrue();
    assertThat(pattern.matcher("duper.-:_234").matches()).isTrue();

    assertThat(pattern.matcher("aser%").matches()).isFalse();
    assertThat(pattern.matcher("&").matches()).isFalse();
    assertThat(pattern.matcher("/").matches()).isFalse();
  }

  @Test
  void findNotMatchingCharFromISORegex() {
    String testString = "test";
    for (char c : testString.toCharArray()) {
      assertTrue(String.valueOf(c).matches(AtlasCharacterSetsRegex.ISO_8859_1),
          "Not matching char: " + c);
    }
  }

  @Test
  void shouldValidateEmailAddressCorrectly() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.EMAIL_ADDRESS);

    assertThat(pattern.matcher("test@here.com").matches()).isTrue();
    assertThat(pattern.matcher("beste.dude@sbb.ch").matches()).isTrue();
    assertThat(pattern.matcher("Beste.dude@sbb.ch").matches()).isTrue();
    assertThat(pattern.matcher(StringUtils.EMPTY).matches()).isTrue();

    assertThat(pattern.matcher("@me").matches()).isFalse();
    assertThat(pattern.matcher("@me.here").matches()).isFalse();
    assertThat(pattern.matcher("#besteLife").matches()).isFalse();
    assertThat(pattern.matcher("hello@asdf").matches()).isFalse();
    assertThat(pattern.matcher("hello@asdf.").matches()).isFalse();
    assertThat(pattern.matcher(" @ .  ").matches()).isFalse();
    assertThat(pattern.matcher("@@.@@").matches()).isFalse();
  }

  @Test
  void shouldValidateAlphaNumericCorrectly() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.ALPHA_NUMERIC);

    assertThat(pattern.matcher("ABK").matches()).isTrue();
    assertThat(pattern.matcher("sbb01").matches()).isTrue();

    assertThat(pattern.matcher(".").matches()).isFalse();
    assertThat(pattern.matcher("hello there").matches()).isFalse();
    assertThat(pattern.matcher("wut_this").matches()).isFalse();
    assertThat(pattern.matcher("hello@asdf").matches()).isFalse();
    assertThat(pattern.matcher("tag me on #atlas").matches()).isFalse();
  }

  @Test
  void shouldValidateAbbreviationPattern() {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.ABBREVIATION_PATTERN);

    assertThat(pattern.matcher("W").matches()).isTrue();
    assertThat(pattern.matcher("BN").matches()).isTrue();

    assertThat(pattern.matcher("GAGG.").matches()).isFalse();
    assertThat(pattern.matcher("BAU,,").matches()).isFalse();
    assertThat(pattern.matcher(":").matches()).isFalse();
    assertThat(pattern.matcher("a").matches()).isFalse();
  }
}
