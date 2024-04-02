package ch.sbb.atlas.auto.rest.doc.snippets;

import java.util.HashMap;
import java.util.Map;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class MethodAndPathSnippet extends TemplatedSnippet {

    public MethodAndPathSnippet() {
        super("method-path", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("method", operation.getRequest().getMethod().name());
        model.put("path", operation.getRequest().getUri().getPath());
        return model;
    }


}