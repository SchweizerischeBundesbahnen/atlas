package ch.sbb.business.organisation.directory;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.atlas.versioning.service.VersionableServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BusinessOrganisationDirectoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessOrganisationDirectoryApplication.class, args);
	}

	@Bean
	public VersionableService versionableService() {
		return new VersionableServiceImpl();
	}

}
