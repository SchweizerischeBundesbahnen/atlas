package ch.sbb.atlas.model.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest
@WithAdminMockJwtAuthentication
@ActiveProfiles("integration-test")
@Import({TestcontainersConfiguration.class, MockKafkaConfig.class})
public @interface IntegrationTest {

}
