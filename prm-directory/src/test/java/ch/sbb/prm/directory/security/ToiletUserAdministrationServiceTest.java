package ch.sbb.prm.directory.security;

import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ToiletUserAdministrationServiceTest extends BaseUserAdministrationServiceTest {

    @Autowired
    public ToiletUserAdministrationServiceTest(SharedServicePointRepository sharedServicePointRepository,
                                               PrmBusinessOrganisationBasedUserAdministrationService prmBOBasedUserAdministrationService,
                                               UserPermissionHolder userPermissionHolder) {
        super(sharedServicePointRepository, prmBOBasedUserAdministrationService, userPermissionHolder);
    }

    @Override
    protected SharedServicePoint getSharedServicePoint() {
        SharedServicePoint servicePoint = SharedServicePoint.builder()
                .servicePoint("{\"servicePointSloid\":\"ch:1.sloid:12345\",\"sboids\":[\"ch:1:sboid:100001\",\"ch:1:sboid:100002\",\"ch:1:sboid:100003\",\"ch:1:sboid:100004\",\"ch:1:sboid:100005\"],"
                        + "\"trafficPointSloids\":[\"ch:1.sloid:12345:1\"]}")
                .sloid("ch:1.sloid:12345")
                .build();
        return servicePoint;
    }

    @Test
    void shouldAllowToiletCreateToAdminUser() {
        allowCreateToAdminUser(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldAllowToiletCreateToSupervisorUser() {
        allowCreateToSupervisorUser(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldAllowToiletCreateToSuperUser() {
        allowCreateToSuperUser(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldNotAllowToiletCreateToReaderUser() {
        notAllowCreateToReaderUser(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldAllowToiletCreateToWriterUserWithAppropriateBO() {
        allowCreateToWriterUserWithAppropriateBO(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldNotAllowToiletCreateToWriterUserWithInappropriateBO() {
        notAllowCreateToWriterUserWithInappropriateBO(ToiletTestData.getToiletVersion());
    }

    @Test
    void shouldAllowToiletCreateToWriterUserWithAppropriateBOs() {
        allowCreateToWriterUserWithAppropriateBOs(ToiletTestData.getToiletVersion());
    }

}
