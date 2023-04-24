package ch.sbb.atlas.apim.configuration;

import ch.sbb.atlas.apim.configuration.StagePublishingConfiguration.StageConfig;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class ApimConfigurationGeneratorApplication implements CommandLineRunner {

  private final OpenApiLoader openApiLoader;
  private final StagePublishingConfiguration publishingConfiguration;

  public static void main(String[] args) {
    SpringApplication.run(ApimConfigurationGeneratorApplication.class, args);
  }

  @Override
  public void run(String... args) {
    log.info("Generating API ... hold on, args={}", Arrays.asList(args));
    if (args.length == 1) {
      publishingConfiguration.getStages().forEach((stage, stageConfig) -> exportApi(args[0], stage, stageConfig));
    } else {
      log.info("No Argument was passed. Skipped execution");
    }
  }

  private void exportApi(String version, String stage, StageConfig stageConfig) {
    log.info("Loading APIs for {} ...", stage);
    Map<String, OpenAPI> openApis = openApiLoader.loadOpenApis(stageConfig);

    log.info("Loaded APIs for {} successfully ...", stage);
    OpenAPI combinedApi = new OpenApiMerger(version, stage, stageConfig).getCombinedApi(openApis);
    log.info("Combined successfully ...");
    OpenApiYamlExporter.export(combinedApi, stageConfig.getExportDirectory());
    log.info("Exported combined API successfully");
  }
}
