package ch.sbb.atlas.auto.rest.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "POINT_OF_SALE")
@RequiredArgsConstructor
@Getter
public enum Category  {

  NOVA_VIRTUAL

}
