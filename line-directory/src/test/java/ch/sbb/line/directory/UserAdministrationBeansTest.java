package ch.sbb.line.directory;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.UserAdministrationLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThatNoException;

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
