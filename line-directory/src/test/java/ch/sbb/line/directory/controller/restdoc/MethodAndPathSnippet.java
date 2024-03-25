package ch.sbb.line.directory.controller.restdoc;

import static ch.sbb.atlas.model.controller.OperationAttributeHelper.getRequestMethod;

import java.util.HashMap;
import java.util.Map;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;

public class MethodAndPathSnippet extends TemplatedSnippet {


    public MethodAndPathSnippet() {
        super("auto-method-path", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
        model.put("method", getRequestMethod(operation));
        model.put("path", operation.getRequest().getUri().getPath());
        return model;
    }


}