package ch.sbb.atlas.model.controller;


import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@EnableAutoConfiguration
@IntegrationTest
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseControllerApiTest {

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper mapper;

  @Autowired
  private WebApplicationContext context;

  protected final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      StandardCharsets.UTF_8);

  @BeforeEach
   void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .alwaysDo(JacksonResultHandlers.prepareJackson(mapper))
        .alwaysDo(commonDocumentation())
        .apply(MockMvcRestDocumentation
            .documentationConfiguration(restDocumentation)
            .uris()
            .withScheme("http")
            .withHost("localhost")
            .withPort(8080)
            .and().snippets()
            .withDefaults(CliDocumentation.curlRequest(),
                HttpDocumentation.httpRequest(),
                HttpDocumentation.httpResponse(),
                AutoDocumentation.requestFields(),
                AutoDocumentation.responseFields(),
                AutoDocumentation.pathParameters(),
                AutoDocumentation.requestParameters(),
                AutoDocumentation.description(),
                AutoDocumentation.methodAndPath(),
                AutoDocumentation.sectionBuilder()
                                 .skipEmpty(true)
                                 .build()))
        .build();
  }

  protected RestDocumentationResultHandler commonDocumentation() {
    return MockMvcRestDocumentation.document("{class-name}/{method-name}",
        preprocessRequest(prettyPrint()),
        preprocessResponse(
            ResponseModifyingPreprocessors.replaceBinaryContent(),
            ResponseModifyingPreprocessors.limitJsonArrayLength(mapper),
            prettyPrint()));
  }

}
