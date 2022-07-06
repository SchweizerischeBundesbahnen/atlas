package ch.sbb.business.organisation.directory.api;


import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Transport Company relations")
@RequestMapping("v1/transport-company-relations")
public interface TransportCompanyRelationApiV1 {

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  TransportCompanyRelationModel createTransportCompanyRelation(@RequestBody @Valid TransportCompanyRelationModel model);

  @GetMapping("{transportCompanyId}")
  List<TransportCompanyBoRelationModel> getTransportCompanyRelations(@PathVariable Long transportCompanyId, @RequestParam String language);

  @DeleteMapping("{relationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteTransportCompanyRelation(@PathVariable Long relationId);

}
