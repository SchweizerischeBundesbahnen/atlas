package ch.sbb.atlas.auto.rest.doc.snippets;

import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptor;
import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptors;
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
//if you are here because the test doesn't work, before losing patience, check that the url and http method are correct ;-)
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