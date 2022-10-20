package ch.sbb.atlas.user.administration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  @Override
  protected RunAsManager runAsManager() {
    RunAsManagerImpl runAsManager = new RunAsManagerImpl();
    runAsManager.setKey("MyRunAsKey");
    return runAsManager;
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    RunAsImplAuthenticationProvider authProvider = new RunAsImplAuthenticationProvider();
    authProvider.setKey("MyRunAsKey");
    auth.authenticationProvider(authProvider);
  }

  //  @Bean
  //  public AuthenticationProvider runAsAuthenticationProvider() {
  //    RunAsImplAuthenticationProvider authProvider = new RunAsImplAuthenticationProvider();
  //    authProvider.setKey("MyRunAsKey");
  //    return authProvider;
  //  }

}