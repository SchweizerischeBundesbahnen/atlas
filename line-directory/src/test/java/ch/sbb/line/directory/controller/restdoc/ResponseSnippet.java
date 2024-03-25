package ch.sbb.line.directory.controller.restdoc;

import java.util.HashMap;
import java.util.Map;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.web.method.HandlerMethod;

public class ResponseSnippet extends TemplatedSnippet {


    public ResponseSnippet() {
        super("auto-response", null);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = new HashMap<>();
//        System.out.println(operation.getAttributes());

        MockHttpServletRequest request = (MockHttpServletRequest) operation.getAttributes().get("org.springframework.mock.web.MockHttpServletRequest");
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping"
            + ".bestMatchingHandler");

        Class<?> responseClass = handlerMethod.getMethod().getReturnType();

        ClassDescriptor classDescriptor = new ClassDescriptor(responseClass);
        return model;
    }


}