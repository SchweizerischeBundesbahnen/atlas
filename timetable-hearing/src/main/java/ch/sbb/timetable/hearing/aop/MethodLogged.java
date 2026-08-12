package ch.sbb.timetable.hearing.aop;

import ch.sbb.timetable.hearing.aop.LoggingAspect.WorkflowType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodLogged {

  WorkflowType workflowType();

}

