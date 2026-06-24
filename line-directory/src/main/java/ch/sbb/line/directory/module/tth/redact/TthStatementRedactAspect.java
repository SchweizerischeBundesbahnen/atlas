package ch.sbb.line.directory.module.tth.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.redact.RedactAspect;
import ch.sbb.atlas.user.administration.security.service.BoUserMailCheckService;
import ch.sbb.atlas.user.administration.security.service.CantonBasedUserAdministrationService;
import ch.sbb.line.directory.module.tth.entity.StatementDocument;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingStatement;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class TthStatementRedactAspect {

  private final BoUserMailCheckService boUserMailCheckService;
  private final CantonBasedUserAdministrationService cantonBasedUserAdministrationService;

  @Around("@annotation(ch.sbb.line.directory.module.tth.redact.TthStatementRedacted)")
  public Object redactSensitiveDataForStatement(ProceedingJoinPoint joinPoint) throws Throwable {
    Object resultObject = joinPoint.proceed();

    return doRedact(resultObject);
  }

  Object doRedact(Object resultObject) {
    boolean mayReadPrivateInfo = cantonBasedUserAdministrationService.isAtLeastExplicitReader(ApplicationType.TIMETABLE_HEARING);
    if (mayReadPrivateInfo) {
      return resultObject;
    }

    Object redactObject = redactObject(resultObject);
    redactStatementForBoUser(resultObject, redactObject);

    return redactObject;
  }

  void redactStatementForBoUser(Object resultObject, Object redactObject) {

    if (resultObject instanceof TimetableHearingStatement timetableHearingStatement
        && timetableHearingStatement.getDossierContactMail() != null
        && boUserMailCheckService.isCurrentUserAssignedTo(timetableHearingStatement)) {
      String statement = timetableHearingStatement.getStatement();

      if (redactObject instanceof TimetableHearingStatement redactedStatement) {
        if (Boolean.TRUE.equals(timetableHearingStatement.getStatementAnonymous())) {
          redactedStatement.setStatement(statement);
        }
        Set<StatementDocument> anonymousDocuments = timetableHearingStatement.getDocuments().stream()
            .filter(document -> Boolean.TRUE.equals(document.getAnonymous()))
            .collect(Collectors.toSet());
        redactedStatement.setDocuments(anonymousDocuments);
      } else {
        throw new IllegalStateException(redactObject + " is not a TimetableHearingStatement");
      }
    }
  }

  static Object redactObject(Object resultObject) {
    return RedactAspect.redactResult(resultObject);
  }

}
