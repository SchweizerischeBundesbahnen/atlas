package ch.sbb.atlas.servicepointdirectory.service.servicepoint.integrationtests;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointVersionConflictException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePointServiceExceptionTest extends BaseServicePointServiceIntegrationTest {

    @Autowired
    public ServicePointServiceExceptionTest(ServicePointVersionRepository versionRepository,
        ServicePointService servicePointService) {
        super(versionRepository, servicePointService);
    }

    @Test
    void shouldThrowConflictExceptionIf() {
        // when
        ServicePointVersion servicePointVersion = versionRepository.save(version1);
        Executable saveExecutable = () -> servicePointService.save(servicePointVersion);

        // then
        Assertions.assertThrows(ServicePointVersionConflictException.class, saveExecutable);
    }

}
