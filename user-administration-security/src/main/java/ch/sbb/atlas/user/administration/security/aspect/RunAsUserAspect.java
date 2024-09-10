package ch.sbb.atlas.user.administration.security.aspect;

import ch.sbb.atlas.service.UserService;
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
    RunAsUser runAsUser = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(RunAsUser.class);
    String userName = (String) joinPoint.getArgs()[0];
    log.info("Create authentication for: {}", userName);

    Jwt accessToken = UserService.getAccessToken();
    Map<String, Object> claims = accessToken.getClaims();
    Map<String, Object> temporaryDownGradedClaims = new HashMap<>();
    claims.forEach((key, value) -> {
        if(key.equals("sbbuid")){
        temporaryDownGradedClaims.put("sbbuid","u242492");
      }
      else if(key.equals("roles")){
        temporaryDownGradedClaims.put("roles", new ArrayList<>());
      }
      else{
        temporaryDownGradedClaims.put(key,value);
      }
    });
    Jwt temporaryDownGradedToken = new Jwt(accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(),
        accessToken.getHeaders(), temporaryDownGradedClaims);
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(temporaryDownGradedToken,
        new ArrayList<>(), userName);

    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    final Object proceed = joinPoint.proceed();
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(accessToken));
    log.info("Disable fake authentication.");
    return proceed;
  }

}
