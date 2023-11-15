package ch.sbb.prm.directory.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@Transactional
public abstract class BasePrmServiceTest {

    protected static final String PARENT_SERVICE_POINT_SLOID = "ch:1:sloid:70000";

    private final SharedServicePointRepository sharedServicePointRepository;

    @Autowired
    public BasePrmServiceTest(SharedServicePointRepository sharedServicePointRepository) {
        this.sharedServicePointRepository = sharedServicePointRepository;
    }

    @BeforeEach
    void setUp() {
        SharedServicePoint servicePoint = SharedServicePoint.builder()
                .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:70000\",\"sboids\":[\"ch:1:sboid:100602\"],"
                        + "\"trafficPointSloids\":[]}")
                .sloid("ch:1:sloid:70000")
                .build();
        sharedServicePointRepository.saveAndFlush(servicePoint);
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
