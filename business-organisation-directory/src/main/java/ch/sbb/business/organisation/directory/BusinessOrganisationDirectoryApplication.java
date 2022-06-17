package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.model.configuration.AtlasExceptionHandler;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class BusinessOrganisationDirectoryApplication {

	public static void main(String[] args) {
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

}
