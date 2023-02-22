package ch.sbb.atlas.base.service.aspect.annotation;

import ch.sbb.atlas.base.service.aspect.FakeUserType;
import ch.sbb.atlas.base.service.aspect.RunAsUserAspect;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Bean;

/**
 * This annotation should be used when a method that creates or updates an entity, extended from {@link BaseEntity}, is executed
 * from a maschine user like a Kafka consumer. This annotation creates a fake user (see {@link RunAsUserAspect}) that can be used
 * to fill {@link BaseEntity#setCreator(String)} or {@link BaseEntity#setEditor(String)}.
 * <p>
 * To use this annotation in your service make sure the {@link RunAsUserAspect} is declared as a {@link Bean}, e.g.:
 * <p>
 * <pre>
 * {@code
 * @Bean
 * public RunAsUserAspect runAsUseAspect() {
 *  return new RunAsUserAspect();
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RunAsUser {

  FakeUserType fakeUserType();

}
