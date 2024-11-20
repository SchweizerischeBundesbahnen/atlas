package ch.sbb.workflow.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RedactAspectTest {

  private RedactAspect redactAspect;

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private RedactDecider redactDecider;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    redactAspect = new RedactAspect(redactDecider);
  }

  @Test
  void shouldRedactNestedValues() throws Throwable {
    //given
    when(redactDecider.shouldRedact(any(), any())).thenReturn(true);
    doReturn(getExampleWithImplicitSboid()).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redactSensitiveData(joinPoint);

    //then
    RedactTarget redactedResult = (RedactTarget) result;
    assertThat(redactedResult).isNotNull();

    RedactTarget expectedResult = RedactTarget.builder()
        .info("Secret Memo")
        .sboid("sboid")
        .mail("r*****")
        .ccEmails(List.of("r*****", "r*****"))
        .examinants(Set.of(NestedRedactTarget.builder()
            .firstName("H*****")
            .lastName("W*****")
            .function("Wildhüter von Hogwarts")
            .mail("h*****")
            .build()))
        .build();
    assertThat(redactedResult).isEqualTo(expectedResult);
  }

  @Test
  void shouldNotRedact() throws Throwable {
    //given
    when(redactDecider.shouldRedact(any(), any())).thenReturn(false);
    doReturn(getExampleWithImplicitSboid()).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redactSensitiveData(joinPoint);

    //then
    RedactTarget redactedResult = (RedactTarget) result;
    assertThat(redactedResult).isNotNull();

    RedactTarget expectedResult = getExampleWithImplicitSboid();
    assertThat(redactedResult).isEqualTo(expectedResult);
  }

  public RedactTarget getExampleWithImplicitSboid() {
    return RedactTarget.builder()
        .info("Secret Memo")
        .sboid("sboid")
        .mail("redacted@sbb.ch")
        .ccEmails(List.of("redact1@bav.ch", "redact2@bav.ch"))
        .examinants(Set.of(NestedRedactTarget.builder()
            .firstName("Hagrid")
            .lastName("Wildhüter")
            .function("Wildhüter von Hogwarts")
            .mail("hagrid@hogwarts.uk")
            .build()))
        .build();
  }

}