package ch.sbb.line.directory.controller.restdoc;

import ch.sbb.line.directory.controller.restdoc.FieldDescriptors.FieldDescriptor;
import java.util.Collections;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class QueryParamsSnippet extends AtlasTableSnippet {

  public QueryParamsSnippet() {
    super("query-params", "Query Parameters");
  }

  @Override
  protected List<FieldDescriptor> getFields(Operation operation) {
    MockHttpServletRequest request = (MockHttpServletRequest) operation.getAttributes()
        .get("org.springframework.mock.web.MockHttpServletRequest");
    HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping"
        + ".bestMatchingHandler");

    List<MethodParameter> parameters = List.of(handlerMethod.getMethodParameters());

    List<MethodParameter> queryParameters = parameters.stream()
        .filter(parameter -> !parameter.hasParameterAnnotation(RequestBody.class))
        .filter(parameter -> !parameter.hasParameterAnnotation(PathVariable.class))
        .toList();
    if (!queryParameters.isEmpty()) {
      FieldDescriptors fieldDescriptors = new FieldDescriptors(queryParameters);
      return fieldDescriptors.getFields();
    }
    return Collections.emptyList();
  }
}