package ch.sbb.atlas.location;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SqlGroup({
    @Sql(scripts = {"/db-creation.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS),
    @Sql(scripts = {"/db-reset-sequence.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
public @interface LocationSchemaCreation {

}
