package ch.sbb.exportservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.IntegrationTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SqlGroup({@Sql(scripts = {"/service-point-schema.sql", "/service-point-init-data.sql"}, executionPhase =
    ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource =
    "servicePointDataSource",
    transactionManager =
        "servicePointTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED)),
    @Sql(scripts = {"/service-point-drop.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD, config =
    @SqlConfig(dataSource = "servicePointDataSource",
        transactionManager =
            "servicePointTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED))

})
@IntegrationTest
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
    assertThat(parentDir).isNotNull();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }

    log.info("Exporting OpenAPI spec.yaml to {}", specYamlFile.toAbsolutePath().normalize());

    byte[] specYamlAsBytes = mvcResult.getResponse().getContentAsByteArray();
    assertThat(specYamlAsBytes).isNotEmpty();

    Files.write(specYamlFile, specYamlAsBytes);
  }

}
