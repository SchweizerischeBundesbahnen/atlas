package ch.sbb.exportservice.job.prm.ersad.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;

class ErsadModelGoldenFilesTest {

  @Test
  void shouldParseTavannesGoldenFileAsWellFormedXml() {
    // Given
    InputStream xml = getClass().getResourceAsStream("/ersad/tavannes-8500100.xml");

    // When / Then
    assertThatCode(() -> DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldParseBurgdorfYverdonBellinzonaGoldenFileAsWellFormedXml() {
    // Given
    InputStream xml = getClass().getResourceAsStream("/ersad/burgdorf-yverdon-bellinzona.xml");

    // When / Then
    assertThatCode(() -> DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldNotContainTotalCapacityElementInGoldenFiles() throws Exception {
    // Given
    String tavannes = new String(getClass().getResourceAsStream("/ersad/tavannes-8500100.xml")
        .readAllBytes());
    String contentAfterAdaptationComment = tavannes.substring(tavannes.indexOf("-->"));

    // When / Then
    assertThat(contentAfterAdaptationComment).doesNotContain("<TotalCapacity>");
  }
}
