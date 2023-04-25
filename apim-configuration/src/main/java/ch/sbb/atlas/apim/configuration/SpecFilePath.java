package ch.sbb.atlas.apim.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SpecFilePath {

  private static final String MVN_RESOURCE_DIR = "src/main/resources";
  private static final Path INTELLIJ_BASE_PATH = Paths.get("apim-configuration/" + MVN_RESOURCE_DIR);

  public static Path getBasePath() {
    if (Files.exists(INTELLIJ_BASE_PATH)) {
      log.info("Using intellij basepath");
      return INTELLIJ_BASE_PATH;
    }
    log.info("Using mvn basepath");
    return Paths.get(MVN_RESOURCE_DIR);
  }

}
