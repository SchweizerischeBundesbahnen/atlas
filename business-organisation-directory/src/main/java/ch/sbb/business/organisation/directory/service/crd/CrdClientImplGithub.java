package ch.sbb.business.organisation.directory.service.crd;

import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("github")
public class CrdClientImplGithub implements CrdClient {

  @Override
  public List<Company> getAllCompanies() {
    return Collections.emptyList();
  }

}