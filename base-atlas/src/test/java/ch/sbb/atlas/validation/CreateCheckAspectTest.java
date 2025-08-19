package ch.sbb.atlas.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.exception.IdProvidedOnCreateException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCheckAspectTest {

  @InjectMocks
  @Spy
  private CreateCheckAspect aspect;

  @Test
  void shouldNotThrowExceptionWhenIdIsProvidedOnCreate() throws Throwable {
    // Given
    DummyEntity bern = DummyEntity.builder()
        .id(null)
        .build();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

    DummyEntity[] args = {bern};
    when(joinPoint.getArgs()).thenReturn(args);

    //when
    aspect.createCheck(joinPoint);

    //then
    verify(joinPoint).proceed();
  }

  @Test
  void shouldThrowExceptionWhenIdIsProvidedOnCreate() throws Throwable {
    // Given
    DummyEntity bern = DummyEntity.builder()
        .id(1111L)
        .build();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

    DummyEntity[] args = {bern};
    when(joinPoint.getArgs()).thenReturn(args);

    assertThrows(IdProvidedOnCreateException.class, () -> aspect.createCheck(joinPoint));

  }
}
