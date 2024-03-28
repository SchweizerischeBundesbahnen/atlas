package ch.sbb.atlas.auto.rest.doc.snippets;

import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptor;
import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptors;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class RequestBodySnippet extends AtlasTableSnippet {

  public RequestBodySnippet() {
    super("request-body", "Request Body Fields");
  }

  @Override
  protected List<FieldDescriptor> getFields(HandlerMethod handlerMethod) {
    List<MethodParameter> parameters = List.of(handlerMethod.getMethodParameters());

    Optional<MethodParameter> requestBody = parameters.stream()
        .filter(parameter -> parameter.hasParameterAnnotation(RequestBody.class))
        .findFirst();
    if (requestBody.isPresent()) {
      return new FieldDescriptors(requestBody.get().getParameterType()).getFields();
    }
    return Collections.emptyList();
  }
}