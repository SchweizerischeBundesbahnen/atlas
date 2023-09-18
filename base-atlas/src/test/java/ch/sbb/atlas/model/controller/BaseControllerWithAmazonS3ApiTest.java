package ch.sbb.atlas.model.controller;

import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.springframework.test.web.servlet.MvcResult;

public abstract class BaseControllerWithAmazonS3ApiTest extends BaseControllerApiTest {

  protected void deleteFileFromBucket(MvcResult mvcResult, String dir, AmazonService amazonService)
      throws UnsupportedEncodingException {
    List<String> responseContent = Arrays.asList(
        mvcResult.getResponse().getContentAsString().split("\\s*,\\s*"));
    responseContent.forEach(s -> {
      String escapedString = s.replace("\"", "").replace("[", "").replace("]", "");
      String filePathToRemove = escapedString.substring(escapedString.lastIndexOf("/"));
      amazonService.deleteFile(AmazonBucket.EXPORT, dir + filePathToRemove);
    });

  }
}
