package ch.sbb.business.organisation.directory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
// TODO: use integrationtest annotation
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class ApimYamlExtractionTest {

  @Autowired
  private MockMvc mvc;

  @Value("${info.app.name}")
  private String appName;

  @Test
  void shouldProvideApiYaml() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/v3/api-docs.yaml"))
                             .andExpect(status().isOk())
                             .andReturn();
    Path specYamlFile = Paths.get("..", "apim-configuration",
        "src/main/resources/apis/", appName, "spec.yaml");

    Path parentDir = specYamlFile.getParent();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }

    log.info("Exporting OpenAPI spec.yaml to {}", specYamlFile.toAbsolutePath().normalize());

    byte[] specYamlAsBytes = mvcResult.getResponse().getContentAsByteArray();
    assertThat(specYamlAsBytes).isNotEmpty();

    Files.write(specYamlFile, specYamlAsBytes);
  }

}
