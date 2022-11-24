package ch.sbb.atlas.apim.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum OpenApiExportConfig {
    DEV("api-dev", "dev"),
    TEST("api-test", "test"),
    INT("api-int", "int"),
    PROD("api-prod", "prod");

    private final String exportDirectory;
    private final String stage;
}
