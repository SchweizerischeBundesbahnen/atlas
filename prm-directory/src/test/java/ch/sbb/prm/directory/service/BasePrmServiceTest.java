package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public abstract class BasePrmServiceTest {

    protected static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";

    private final SharedServicePointRepository sharedServicePointRepository;
    @MockBean
    protected final PrmLocationService prmLocationService;

    @Autowired
    public BasePrmServiceTest(SharedServicePointRepository sharedServicePointRepository, PrmLocationService prmLocationService) {
        this.sharedServicePointRepository = sharedServicePointRepository;
        this.prmLocationService = prmLocationService;
    }

    @BeforeEach
    void setUp() {
        sharedServicePointRepository.saveAndFlush(SharedServicePointTestData.getSharedServicePoint());
    }

    @AfterEach
    void cleanUp() {
        sharedServicePointRepository.deleteAll();
    }

    @Test
    void shouldVerifyRepoIsNotEmpty() {
        List<SharedServicePoint> servicePointList = sharedServicePointRepository.findAll();
        assertThat(servicePointList.isEmpty()).isFalse();
    }

}
