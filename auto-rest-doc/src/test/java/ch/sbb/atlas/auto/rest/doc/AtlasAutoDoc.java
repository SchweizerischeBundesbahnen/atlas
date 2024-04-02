package ch.sbb.atlas.auto.rest.doc;

import ch.sbb.atlas.auto.rest.doc.snippets.AtlasAutoDocSnippet;
import ch.sbb.atlas.auto.rest.doc.snippets.MethodAndPathSnippet;
import ch.sbb.atlas.auto.rest.doc.snippets.PathVariablesSnippet;
import ch.sbb.atlas.auto.rest.doc.snippets.QueryParamsSnippet;
import ch.sbb.atlas.auto.rest.doc.snippets.RequestBodySnippet;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.web.method.HandlerMethod;

@UtilityClass
public class AtlasAutoDoc {

  public static Snippet[] configure() {
    return new Snippet[]{
        CliDocumentation.curlRequest(),
        HttpDocumentation.httpRequest(),
        HttpDocumentation.httpResponse(),
        new MethodAndPathSnippet(),
        new PathVariablesSnippet(),
        new QueryParamsSnippet(),
        new RequestBodySnippet(),
        new AtlasAutoDocSnippet()
    };
  }

  public static RestDocumentationContext getDocumentationContext(Operation operation) {
    return (RestDocumentationContext) operation.getAttributes().get(RestDocumentationContext.class.getName());
  }

  public static HandlerMethod getHandlerMethod(Operation operation) {
    MockHttpServletRequest request = (MockHttpServletRequest) operation.getAttributes()
        .get("org.springframework.mock.web.MockHttpServletRequest");
    return (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");
  }

}