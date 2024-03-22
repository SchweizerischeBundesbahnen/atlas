package ch.sbb.atlas.model.controller;

import static ch.sbb.atlas.model.controller.OperationAttributeHelper.getRequestMethod;
import static ch.sbb.atlas.model.controller.OperationAttributeHelper.getRequestPattern;

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
        model.put("path", nullToEmpty(getRequestPattern(operation)));
        return model;
    }

    private String nullToEmpty(String s) {
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}