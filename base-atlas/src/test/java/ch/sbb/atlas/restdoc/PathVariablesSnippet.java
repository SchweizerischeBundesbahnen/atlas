package ch.sbb.atlas.restdoc;

import ch.sbb.atlas.restdoc.FieldDescriptors.FieldDescriptor;
import java.util.Collections;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;

public class PathVariablesSnippet extends AtlasTableSnippet {

  public PathVariablesSnippet() {
    super("path-variables", "Path Variables");
  }

  @Override
  protected List<FieldDescriptor> getFields(HandlerMethod handlerMethod) {
    List<MethodParameter> parameters = List.of(handlerMethod.getMethodParameters());

    List<MethodParameter> pathVariables = parameters.stream()
        .filter(parameter -> parameter.hasParameterAnnotation(PathVariable.class))
        .toList();
    if (!pathVariables.isEmpty()) {
      FieldDescriptors fieldDescriptors = new FieldDescriptors(pathVariables);
      fieldDescriptors.getFields().forEach(i -> i.setOptional(false));
      return fieldDescriptors.getFields();
    }
    return Collections.emptyList();
  }

}