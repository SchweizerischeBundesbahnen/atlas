package ch.sbb.line.directory.module.line.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * The cableway fields (Anlagennummer, Anlage-Typ, Anlage-Name) describe a Seilbahn installation. They may only be set on
 * versions whose meansOfTransport contain CABLE_RAILWAY.
 */
@RequiredArgsConstructor
@Getter
public class LineV3CablewayFieldNotAllowedException extends AtlasException {

  private static final String ERROR_CODE = "LIDI.LINE.ERROR.CABLEWAY.FIELD_NOT_ALLOWED";
  private static final String ERROR = "Cableway field not allowed without mean of transport CABLE_RAILWAY";

  private final Set<String> fields;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Cableway fields must not be set without mean of transport CABLE_RAILWAY: " + new TreeSet<>(fields))
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return fields.stream()
        .map(field -> Detail.builder()
            .field(field)
            .message("Field {0} may only be set if meansOfTransport contains CABLE_RAILWAY")
            .displayInfo(builder()
                .code(ERROR_CODE)
                .with("field", field)
                .build())
            .build())
        .collect(Collectors.toSet());
  }

}
