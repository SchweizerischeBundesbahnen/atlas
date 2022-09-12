package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.base.service.amazon.service.FileService;
import ch.sbb.atlas.base.service.amazon.service.FileServiceImpl;
import ch.sbb.atlas.base.service.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.base.service.model.service.KafkaTruststorePreparation;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import ch.sbb.atlas.base.service.versioning.service.VersionableServiceImpl;
import java.time.ZoneId;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class BusinessOrganisationDirectoryApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
        KafkaTruststorePreparation.setupTruststore();
        SpringApplication.run(BusinessOrganisationDirectoryApplication.class, args);
    }

    @Bean
    public VersionableService versionableService() {
        return new VersionableServiceImpl();
    }

    @Bean
    public AtlasExceptionHandler atlasExceptionHandler() {
        return new AtlasExceptionHandler();
    }

    @Bean
    public FileService fileService() {
        return new FileServiceImpl();
    }
}
