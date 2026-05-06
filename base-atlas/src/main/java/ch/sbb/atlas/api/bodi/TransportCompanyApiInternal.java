package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[INTERNAL] Transport Companies")
public interface TransportCompanyApiInternal {

  String BASE_PATH = "internal/transport-companies";

  @AdminOnly
  @PostMapping(BASE_PATH + "/loadFromBAV")
  void loadTransportCompaniesFromBav();

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping(BASE_PATH + "/bySboid")
  List<TransportCompanyModel> getTransportCompaniesBySboid(@Parameter @RequestParam String sboid);

}