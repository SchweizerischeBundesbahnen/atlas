package ch.sbb.timetable.field.number;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest
@WithMockJwtAuthentication
@ActiveProfiles("integration-test")
public @interface IntegrationTest {

}
