package ch.sbb.atlas.auto.rest.doc.snippets;

import ch.sbb.atlas.auto.rest.doc.AtlasAutoDoc;
import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public abstract class AtlasTableSnippet extends TemplatedSnippet {

  private final String tableTitle;

  public AtlasTableSnippet(String snippetName, String tableTitle) {
    super(snippetName, null);
    this.tableTitle = tableTitle;
  }

  protected abstract List<FieldDescriptor> getFields(HandlerMethod handlerMethod);

  @Override
  protected Map<String, Object> createModel(Operation operation) {
    Map<String, Object> model = new HashMap<>();
    List<Map<String, Object>> tableContent = createTableContent(getFields(AtlasAutoDoc.getHandlerMethod(operation)));

    model.put("title", tableTitle);
    model.put("content", tableContent);
    model.put("hasContent", !tableContent.isEmpty());
    return model;
  }

  private static List<Map<String, Object>> createTableContent(List<FieldDescriptor> fields) {
    return fields.stream().map(AtlasTableSnippet::createModelForDescriptor).toList();
  }

  private static Map<String, Object> createModelForDescriptor(FieldDescriptor descriptor) {
    Map<String, Object> result = new HashMap<>();
    result.put("path", descriptor.getName() == null ? "" : descriptor.getName());
    result.put("type", descriptor.getType());
    result.put("optional", descriptor.isOptional());
    result.put("description", descriptor.getDescription().replaceAll("\\|", "\\\\|").replaceAll(" #", " \\\\#"));
    return result;
  }
}
