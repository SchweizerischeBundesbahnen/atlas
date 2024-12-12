package ch.sbb.atlas.auto.rest.doc.snippets;

import ch.sbb.atlas.auto.rest.doc.AtlasAutoDoc;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class AtlasAutoDocSnippet extends TemplatedSnippet {

  public AtlasAutoDocSnippet() {
    super("atlas-doc", null);
  }

  @Override
  protected Map<String, Object> createModel(Operation operation) {
    Map<String, Object> model = new HashMap<>();
    model.put("link", buildLink(operation));
    return model;
  }

  private static String buildLink(Operation operation) {
    RestDocumentationContext documentationContext = AtlasAutoDoc.getDocumentationContext(operation);
    return StringUtils.join(
        StringUtils.splitByCharacterTypeCamelCase(documentationContext.getTestMethodName()),
        '-').toLowerCase();
  }

}