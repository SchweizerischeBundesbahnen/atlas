package ch.sbb.exportservice;

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

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Sql(scripts = {"/bodi-schema.sql",
    "/bodi-data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(dataSource =
    "businessOrganisationDirectoryDataSource", transactionManager = "businessOrganisationDirectoryTransactionManager",
    transactionMode = SqlConfig.TransactionMode.ISOLATED))
public @interface BoDiDbSchemaCreation {

}
