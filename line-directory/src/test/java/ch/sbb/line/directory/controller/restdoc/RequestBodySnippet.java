package ch.sbb.line.directory.controller.restdoc;

import ch.sbb.line.directory.controller.restdoc.FieldDescriptors.FieldDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class RequestBodySnippet extends AtlasTableSnippet {

  public RequestBodySnippet() {
    super("request-body", "Request Body Fields");
  }

  @Override
  protected List<FieldDescriptor> getFields(Operation operation) {
    MockHttpServletRequest request = (MockHttpServletRequest) operation.getAttributes()
        .get("org.springframework.mock.web.MockHttpServletRequest");
    HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping"
        + ".bestMatchingHandler");

    List<MethodParameter> parameters = List.of(handlerMethod.getMethodParameters());

    Optional<MethodParameter> requestBody = parameters.stream()
        .filter(parameter -> parameter.hasParameterAnnotation(RequestBody.class))
        .findFirst();
    if (requestBody.isPresent()) {
      FieldDescriptors fieldDescriptors = new FieldDescriptors(requestBody.get().getParameterType());
      return fieldDescriptors.getFields();
    }
    return Collections.emptyList();
  }
}