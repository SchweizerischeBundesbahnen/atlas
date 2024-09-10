package ch.sbb.atlas.user.administration.security.aspect;

import static ch.sbb.atlas.configuration.Role.ROLES_JWT_KEY;
import static ch.sbb.atlas.service.UserService.SBBUID_CLAIM;

import ch.sbb.atlas.service.UserService;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RunAsUserAspect {

  @Around("@annotation(ch.sbb.atlas.user.administration.security.aspect.RunAsUser)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String userName = resolveRunAsUserParameter(joinPoint);
    if (userName != null) {
      log.info("Create temporary claims (downgraded) authentication for user: {}", userName);
      Jwt accessToken = UserService.getAccessToken();
      Jwt temporaryDownGradedToken = getTemporaryDownGradedToken(userName, accessToken);
      JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(temporaryDownGradedToken,
          new ArrayList<>(), userName);
      SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
      log.info("Successfully created temporary claims (downgraded) authentication for user: {}", userName);
      log.info("From now on, all operations will be executed in name of the user: {}", userName);
      final Object proceed = joinPoint.proceed();
      SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(accessToken));
      log.info("Disable temporary authentication.");
      return proceed;
    }
    throw new IllegalStateException("No UserName provided!");
  }

  private static Jwt getTemporaryDownGradedToken(String userName, Jwt accessToken) {
    Map<String, Object> claims = accessToken.getClaims();
    Map<String, Object> temporaryDownGradedClaims = new HashMap<>();
    claims.forEach((key, value) -> {
      if (key.equals(SBBUID_CLAIM)) {
        temporaryDownGradedClaims.put(SBBUID_CLAIM, userName);
      } else if (key.equals(ROLES_JWT_KEY)) {
        temporaryDownGradedClaims.put(ROLES_JWT_KEY, new ArrayList<>());
      } else {
        temporaryDownGradedClaims.put(key, value);
      }
    });
    return new Jwt(accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(),
        accessToken.getHeaders(), temporaryDownGradedClaims);
  }

  String resolveRunAsUserParameter(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();

    Annotation[][] parameterAnnotations = joinPoint.getTarget()
        .getClass()
        .getMethod(method.getName(), parameterTypes)
        .getParameterAnnotations();

    for (int i = 0; i < joinPoint.getArgs().length; i++) {
      Annotation[] annotations = parameterAnnotations[i];

      for (Annotation a : annotations) {
        if (a.annotationType() == RunAsUserParameter.class) {
          if (joinPoint.getArgs()[i] instanceof String) {
            return (String) joinPoint.getArgs()[i];
          }
          throw new IllegalStateException("Parameter marked with @RunAsUserParameter must be a String!");
        }
      }
    }
    throw new IllegalStateException("You have to mark @RunAsUserParameter the userName parameter for the method annotated with "
        + "@RunAsUser!");
  }

}
