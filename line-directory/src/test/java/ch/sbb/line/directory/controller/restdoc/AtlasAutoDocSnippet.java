package ch.sbb.line.directory.controller.restdoc;

import java.util.HashMap;
import java.util.Map;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class AtlasAutoDocSnippet extends TemplatedSnippet {

    public AtlasAutoDocSnippet() {
        super("atlas-doc", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Title");
        model.put("link", "method-link");
        return model;
    }

}