package ch.sbb.line.directory.controller.restdoc;

import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.alternativeName;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.businessOrganisation;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.combinationName;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.lineType;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.longName;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.paymentType;
import static ch.sbb.atlas.api.lidi.LineVersionModel.Fields.swissLineNumber;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.controller.LineController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@EnableAutoConfiguration
@IntegrationTest
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureMockMvc(addFilters = false)
public class RestDocTest {

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private LineController lineController;

  protected final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      StandardCharsets.UTF_8);

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(commonDocumentation())
        .apply(MockMvcRestDocumentation
            .documentationConfiguration(restDocumentation)
            .uris()
            .withScheme("http")
            .withHost("localhost")
            .withPort(8080)
            .and().snippets()
            .withDefaults(AtlasAutoDoc.configure()))
        .build();
  }

  protected RestDocumentationResultHandler commonDocumentation() {
    return MockMvcRestDocumentation.document("{class-name}/{method-name}",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()));
  }

  @Test
  void shouldGetLineOverview() throws Exception {
    //given
    LineVersionModel lineVersionModel = LineTestData.lineVersionModelBuilder().build();
    lineController.createLineVersion(lineVersionModel);

    //when
    mvc.perform(get("/v1/lines")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissLineNumber,asc"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldUpdateLineVersion() throws Exception {
    //given
    LineVersionModel lineVersionModel =
        LineTestData.lineVersionModelBuilder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .alternativeName("alternative")
            .combinationName("combination")
            .longName("long name")
            .lineType(LineType.TEMPORARY)
            .paymentType(PaymentType.LOCAL)
            .swissLineNumber("b0.IC2")
            .build();
    LineVersionModel lineVersionSaved = lineController.createLineVersion(lineVersionModel);
    //when
    lineVersionSaved.setBusinessOrganisation("PostAuto");
    mvc.perform(post("/v1/lines/versions/" + lineVersionSaved.getId().toString())
            .contentType(contentType)
            .content(mapper.writeValueAsString(lineVersionSaved))
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + businessOrganisation, is("PostAuto")))
        .andExpect(jsonPath("$[0]." + alternativeName, is("alternative")))
        .andExpect(jsonPath("$[0]." + combinationName, is("combination")))
        .andExpect(jsonPath("$[0]." + longName, is("long name")))
        .andExpect(jsonPath("$[0]." + lineType, is(LineType.TEMPORARY.toString())))
        .andExpect(jsonPath("$[0]." + paymentType, is(PaymentType.LOCAL.toString())))
        .andExpect(jsonPath("$[0]." + swissLineNumber, is("b0.IC2")))
        .andExpect(jsonPath("$[0]." + businessOrganisation, is("PostAuto")));
  }

}
