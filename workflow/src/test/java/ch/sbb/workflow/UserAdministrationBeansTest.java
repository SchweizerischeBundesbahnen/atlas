package ch.sbb.workflow;

import ch.sbb.atlas.base.service.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.UserAdministrationLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.assertj.core.api.Assertions.assertThatNoException;

@EmbeddedKafka(topics = {"atlas.mail", "atlas.workflow"})
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class UserAdministrationBeansTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldFindUserAdminstrationBeans() {
        assertThatNoException().isThrownBy(
                () -> applicationContext.getBean(UserAdministrationLoader.class));
    }

}
