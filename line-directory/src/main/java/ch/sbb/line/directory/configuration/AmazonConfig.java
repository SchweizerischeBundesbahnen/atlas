package ch.sbb.line.directory.configuration;

import static ch.sbb.atlas.amazon.config.AmazonAtlasConfig.configureAmazonS3Client;

import ch.sbb.atlas.amazon.config.AmazonConfigProps;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.AmazonServiceImpl;
import ch.sbb.atlas.amazon.service.FileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {

    public static final String EXPORT_FILES = "export-files";
    public static final String HEARING_DOCUMENTS = "hearing-documents";

    @Value("${amazon.bucketConfigs.export-files.bucketName}")
    private String bucketNameExportFiles;

    @Value("${amazon.bucketConfigs.hearing-documents.bucketName}")
    private String bucketNameHearingDocument;

    @Bean
    @ConfigurationProperties(prefix = "amazon")
    public AmazonConfigProps amazonConfigProps() {
        return new AmazonConfigProps();
    }

    @Bean
    @Qualifier(EXPORT_FILES)
    public AmazonService amazonExportService(FileService fileService) {
        return new AmazonServiceImpl(configureAmazonS3Client(amazonConfigProps(), EXPORT_FILES), fileService, bucketNameExportFiles);
    }

    @Bean
    @Qualifier(HEARING_DOCUMENTS)
    public AmazonService amazonHearingDocumentService(FileService fileService) {
        return new AmazonServiceImpl(configureAmazonS3Client(amazonConfigProps(), HEARING_DOCUMENTS), fileService, bucketNameHearingDocument);
    }

}
