package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class ApimConfigurationGeneratorApplication implements CommandLineRunner {

    private final OpenApiLoader openApiLoader;
    private final ProductionConfiguration productiveApiConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(ApimConfigurationGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Generating API ... hold on, args={}", Arrays.asList(args));
        if (args.length == 1) {
            Stream.of(OpenApiExportConfig.values()).forEach(openApiExportConfig -> exportApi(args[0], openApiExportConfig));
        } else {
            log.info("No Argument was passed. Skipped execution");
        }
    }

    private void exportApi(String version, OpenApiExportConfig openApiExportConfig) {
        Map<String, OpenAPI> openApis = openApiExportConfig == OpenApiExportConfig.PROD ? openApiLoader.loadProductiveApisOnly() : openApiLoader.loadAllApis();

        log.info("Loaded successfully ...");
        OpenAPI combinedApi = new OpenApiMerger(version, openApiExportConfig, productiveApiConfiguration).getCombinedApi(openApis);
        log.info("Combined successfully ...");
        OpenApiYamlExporter.export(combinedApi, openApiExportConfig.getExportDirectory());
        log.info("Exported combined API successfully");
    }
}
