package ch.sbb.business.organisation.directory.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Business Organisation - Transport Company links")
@RequestMapping("v1/bo-tc-links")
public interface BoTcLinkApiV1 {

  @PostMapping("add")
  @ResponseStatus(HttpStatus.CREATED)
  BoTcLinkModel createBoTcLink(@RequestBody @Valid BoTcLinkModel model);

}
