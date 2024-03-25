package ch.sbb.line.directory.controller.restdoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.web.method.HandlerMethod;

@RequiredArgsConstructor
public class TypeMapper implements ResultHandler {

  private final ObjectMapper mapper;

  @Override
  public void handle(MvcResult result) throws Exception {
    System.out.println(result);

    HandlerMethod handlerMethod = (HandlerMethod) result.getHandler();
    handlerMethod.getMethod();

    getAttributeMap(result).put("method", handlerMethod.getMethod());
  }

  private Map<String, Object> getAttributeMap(MvcResult result) {
    return (Map<String, Object>) result.getRequest().getAttribute("org.springframework.restdocs.configuration");
  }

}
