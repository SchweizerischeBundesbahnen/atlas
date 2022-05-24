package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Arrays;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApimConfigurationGeneratorApplication implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      ApimConfigurationGeneratorApplication.class);

  @Autowired
  private OpenApiLoader openApiLoader;

  public static void main(String[] args) {
    SpringApplication.run(ApimConfigurationGeneratorApplication.class, args);
  }

  @Override
  public void run(String... args) {
    LOGGER.info("Generating API ... hold on, args={}", Arrays.asList(args));
    if (args.length == 1) {
      generateProductiveApis(args);
      generateAllApis(args);
    } else {
      LOGGER.info("No Argument was passed. Skipped execution");
    }
  }

  private void generateProductiveApis(String[] args) {
    exportApi(args, openApiLoader.loadProductiveApisOnly(), new OpenApiYamlExporter(true));
  }

  private void generateAllApis(String[] args) {
    exportApi(args, openApiLoader.loadAllApis(), new OpenApiYamlExporter(false));
  }

  private void exportApi(String[] args, Map<String, OpenAPI> openApis,
      OpenApiYamlExporter exporter) {
    LOGGER.info("Loaded successfully ...");
    OpenAPI combinedApi = new OpenApiMerger(args[0]).getCombinedApi(openApis);
    LOGGER.info("Combined successfully ...");
    exporter.export(combinedApi);
    LOGGER.info("Exported combined API successfully");
  }
}
