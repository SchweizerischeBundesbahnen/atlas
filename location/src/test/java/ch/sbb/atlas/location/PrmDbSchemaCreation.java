package ch.sbb.atlas.location;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SqlGroup({
    @Sql(scripts = {"/stop-point-schema.sql"}, executionPhase =
        ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource =
        "prmDataSource",
        transactionManager =
            "prmTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED)),
    @Sql(scripts = {"/stop-point-drop.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD, config =
    @SqlConfig(dataSource = "prmDataSource",
        transactionManager =
            "prmTransactionManager", transactionMode = SqlConfig.TransactionMode.ISOLATED))
})
public @interface PrmDbSchemaCreation {

}
