package ch.sbb.atlas.restdoc;

import ch.sbb.atlas.restdoc.FieldDescriptors.FieldDescriptor;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

public class QueryParamsSnippet extends AtlasTableSnippet {

  public QueryParamsSnippet() {
    super("query-params", "Query Parameters");
  }

  @Override
  protected List<FieldDescriptor> getFields(HandlerMethod handlerMethod) {
    List<MethodParameter> parameters = List.of(handlerMethod.getMethodParameters());

    Optional<MethodParameter> parameterObject = parameters.stream()
        .filter(parameter -> parameter.hasParameterAnnotation(ParameterObject.class)).findFirst();

    List<MethodParameter> queryParameters = parameters.stream()
        .filter(parameter -> !parameter.hasParameterAnnotation(RequestBody.class))
        .filter(parameter -> !parameter.hasParameterAnnotation(PathVariable.class))
        .filter(parameter -> !parameter.hasParameterAnnotation(ParameterObject.class))
        .filter(parameter -> {
          Parameter parameterAnnotation = parameter.getParameterAnnotation(Parameter.class);
          return parameterAnnotation == null || !parameterAnnotation.hidden();
        })
        .toList();
    if (!queryParameters.isEmpty() || parameterObject.isPresent()) {
      FieldDescriptors fieldDescriptors = new FieldDescriptors(queryParameters);
      List<FieldDescriptor> fields = fieldDescriptors.getFields();
      parameterObject.ifPresent(i -> fields.addAll(getParameterObjectDescriptions(i)));
      fields.addAll(getPageableDescriptions(parameters));
      return fields;
    }
    return Collections.emptyList();
  }

  private List<FieldDescriptor> getParameterObjectDescriptions(MethodParameter parameterObject) {
    return new ArrayList<>(new FieldDescriptors(parameterObject.getParameterType()).getFields());
  }

  private List<FieldDescriptor> getPageableDescriptions(List<MethodParameter> parameters) {
    List<FieldDescriptor> fields = new ArrayList<>();
    if (parameters.stream().anyMatch(i -> i.getParameterType().equals(Pageable.class))) {
      fields.add(FieldDescriptor.builder().fieldName("page").type("Integer").optional(true).build());
      fields.add(FieldDescriptor.builder().fieldName("size").type("Integer").optional(true).build());
      fields.add(FieldDescriptor.builder().fieldName("sort").type("Array[String]").optional(true).build());
    }
    return fields;
  }
}