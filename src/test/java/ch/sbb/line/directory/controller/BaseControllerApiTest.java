package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseControllerApiTest {

  @Autowired
  protected MockMvc mvc;

  @Autowired
  protected ObjectMapper mapper;

  protected final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      StandardCharsets.UTF_8);

}
