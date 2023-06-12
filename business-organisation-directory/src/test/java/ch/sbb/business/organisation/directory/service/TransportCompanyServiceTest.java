package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransportCompanyServiceTest {

  @Mock
  private TransportCompanyRepository transportCompanyRepository;
  @Mock
  private TransportCompanyClient transportCompanyClient;
  @Mock
  private TransportCompanyDistributor transportCompanyDistributor;
  @Mock
  private MailClient mailClient;

  private TransportCompanyService transportCompanyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transportCompanyService = new TransportCompanyService(transportCompanyClient,
        transportCompanyRepository, transportCompanyDistributor, mailClient);
  }

  @Test
  void shouldSaveTransportCompaniesAndValidateRelations() {
    // Given
    String csv = "ID;Initialen;TU-Nummer;Amtl. Bezeichnung;HR-Name/Körperschaft;Status TU;HR-Nr.;UID;RICS-Code;GO-Nr.;Kommentar;\n";
    csv += "\"264\";\"(LBBD)\";\"#0696\";\"Luftseilbahn Betten FO - Betten Dorf\";\"Munizipalgemeinde Betten\";\"5\";\"\";\"CHE-114.857.964\";\"\";\"\";\"Gemeindefusion Bettmeralp; Konzession übertragen\";\n";

    when(transportCompanyClient.getTransportCompanies()).thenReturn(
        Response.builder()
                .body(csv.getBytes(StandardCharsets.UTF_8))
                .request(Request.create(HttpMethod.GET, "http://url.com",
                    Collections.emptyMap(), null, null, null))
                .build());
    when(transportCompanyRepository.findTransportCompaniesWithInvalidRelations()).thenReturn(
        List.of(
            TransportCompany.builder()
                            .id(5L)
                            .description("Beste Company")
                            .number("#0001")
                            .enterpriseId("enterprisige ID")
                            .transportCompanyStatus(TransportCompanyStatus.OPERATOR)
                            .build()));

    // When
    transportCompanyService.saveTransportCompaniesFromBav();

    // Then
    verify(transportCompanyRepository).saveAll(anyList());
    verify(mailClient).produceMailNotification(any());
  }

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