package ch.sbb.atlas.configuration.handler;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AtlasAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    SortedSet<Detail> details = new TreeSet<>();
    details.add(Detail.builder()
        .message(accessDeniedException.getMessage())
        .displayInfo(
            DisplayInfo.builder()
                .code("ERROR.NOTALLOWED")
                .build())
        .build());
    ErrorResponse errorResponse = ErrorResponse.builder().status(HttpStatus.FORBIDDEN.value())
        .message(
            "You are not allowed to perform this operation on the ATLAS platform.")
        .error("Access denied")
        .details(details).build();

    ObjectMapper mapper = new ObjectMapper();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.getWriter()
        .write(mapper.writeValueAsString(errorResponse));
  }
}
