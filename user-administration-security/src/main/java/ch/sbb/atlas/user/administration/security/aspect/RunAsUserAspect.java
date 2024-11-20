package ch.sbb.atlas.user.administration.security.aspect;

import static ch.sbb.atlas.configuration.Role.ROLES_JWT_KEY;
import static ch.sbb.atlas.service.UserService.SBBUID_CLAIM;

import ch.sbb.atlas.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RunAsUserAspect {

  public static final String AUD_CLAIM = "aud";

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
      switch (key) {
        case SBBUID_CLAIM -> temporaryDownGradedClaims.put(SBBUID_CLAIM, userName);
        case ROLES_JWT_KEY -> temporaryDownGradedClaims.put(ROLES_JWT_KEY, new ArrayList<>());
        case AUD_CLAIM -> temporaryDownGradedClaims.put(AUD_CLAIM, new ArrayList<>());
        default -> temporaryDownGradedClaims.put(key, value);
      }
    });
    if (claims.get(SBBUID_CLAIM) == null) {
      temporaryDownGradedClaims.put(SBBUID_CLAIM, userName);
    }
    return new Jwt(accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(),
        accessToken.getHeaders(), temporaryDownGradedClaims);
  }

  String resolveRunAsUserParameter(ProceedingJoinPoint joinPoint) {
    Optional<Object> parameterValue = ParameterInfoReflectionResolver.valueByAnnotation(joinPoint, RunAsUserParameter.class);
    if (parameterValue.isPresent()) {
      if (parameterValue.get() instanceof String stringValue) {
        return stringValue;
      }
      throw new IllegalStateException("Parameter marked with @RunAsUserParameter must be a String!");
    }

    throw new IllegalStateException("You have to mark @RunAsUserParameter the userName parameter for the method annotated with "
        + "@RunAsUser!");
  }

}
