package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.model.controller.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TransportCompanyServiceTest {

  @Autowired
  private TransportCompanyService transportCompanyService;


  @Test
  void shouldParseCsvFileFromBavCorrectly() throws IOException {
    // Given
    InputStream resourceAsStream = TransportCompanyServiceTest.class.getResourceAsStream(
        "/transportCompanies.csv");

    // When
    List<TransportCompanyCsvModel> transportCompanies = TransportCompanyService.parseTransportCompanies(
        resourceAsStream);

    // Then
    assertThat(transportCompanies).hasSize(3490);
  }

  @Test
  void shouldBeAbleToSaveAllBavTransportCompanies() throws IOException {
    InputStream resourceAsStream = TransportCompanyServiceTest.class.getResourceAsStream(
        "/transportCompanies.csv");
    List<TransportCompanyCsvModel> companies = TransportCompanyService.parseTransportCompanies(
        resourceAsStream);
    assertDoesNotThrow(() -> transportCompanyService.saveTransportCompanies(companies));
  }

  @Test
  void shouldParseCsvWithNullFields() throws IOException {
    // Given
    String csv = "ID;TU-Nummer;Initialen;Amtl. Bezeichnung;HR-Name/Körperschaft;Status TU;HR-Nr.;UID;RICS-Code;GO-Nr.;Kommentar;Noch ein Feld\n";
    csv += "\"264\";\"#0696\";\"(LBBD)\";\"Luftseilbahn Betten FO - Betten Dorf\";\"Munizipalgemeinde Betten\";\"5\";\"\";\"CHE-114.857.964\";\"\";\"\";\"Gemeindefusion Bettmeralp; Konzession übertragen\";\"asdf\"\n";

    // When
    List<TransportCompanyCsvModel> transportCompanies = TransportCompanyService.parseTransportCompanies(
        toInputStream(csv));
    // Then
    assertThat(transportCompanies).isNotEmpty();
    assertThat(transportCompanies.get(0).getRicsCode()).isNull();
  }

  @Test
  void shouldParseCsvWithAdditionalFieldAtTheEndIgnoringIt() throws IOException {
    // Given
    String csv = "ID;TU-Nummer;Initialen;Amtl. Bezeichnung;HR-Name/Körperschaft;Status TU;HR-Nr.;UID;RICS-Code;GO-Nr.;Kommentar;Noch ein Feld\n";
    csv += "\"264\";\"#0696\";\"(LBBD)\";\"Luftseilbahn Betten FO - Betten Dorf\";\"Munizipalgemeinde Betten\";\"5\";\"\";\"CHE-114.857.964\";\"\";\"\";\"Gemeindefusion Bettmeralp; Konzession übertragen\";\"asdf\"\n";

    // When
    List<TransportCompanyCsvModel> transportCompanies = TransportCompanyService.parseTransportCompanies(
        toInputStream(csv));
    // Then
    assertThat(transportCompanies).isNotEmpty();
  }

  @Test
  void shouldParseCsvWithAdditionalFieldAsSecondColumn() throws IOException {
    // Given
    String csv = "ID;Neues Feld;TU-Nummer;Initialen;Amtl. Bezeichnung;HR-Name/Körperschaft;Status TU;HR-Nr.;UID;RICS-Code;GO-Nr.;Kommentar;\n";
    csv += "\"264\";\"neues Feld\";\"#0696\";\"(LBBD)\";\"Luftseilbahn Betten FO - Betten Dorf\";\"Munizipalgemeinde Betten\";\"5\";\"\";\"CHE-114.857.964\";\"\";\"\";\"Gemeindefusion Bettmeralp; Konzession übertragen\";\n";

    // When
    List<TransportCompanyCsvModel> transportCompanies = TransportCompanyService.parseTransportCompanies(
        toInputStream(csv));
    // Then
    assertThat(transportCompanies).isNotEmpty();
  }

  @Test
  void shouldParseCsvWithRearrangedColumn() throws IOException {
    // Given
    String csv = "ID;Initialen;TU-Nummer;Amtl. Bezeichnung;HR-Name/Körperschaft;Status TU;HR-Nr.;UID;RICS-Code;GO-Nr.;Kommentar;\n";
    csv += "\"264\";\"(LBBD)\";\"#0696\";\"Luftseilbahn Betten FO - Betten Dorf\";\"Munizipalgemeinde Betten\";\"5\";\"\";\"CHE-114.857.964\";\"\";\"\";\"Gemeindefusion Bettmeralp; Konzession übertragen\";\n";

    // When
    List<TransportCompanyCsvModel> transportCompanies = TransportCompanyService.parseTransportCompanies(
        toInputStream(csv));
    // Then
    assertThat(transportCompanies).isNotEmpty();
    assertThat(transportCompanies.get(0).getAbbreviation()).isEqualTo("(LBBD)");
  }

  private InputStream toInputStream(String string) {
    return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
  }
}