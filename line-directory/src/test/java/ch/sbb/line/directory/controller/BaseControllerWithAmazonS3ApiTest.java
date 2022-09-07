package ch.sbb.line.directory.controller;

import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import com.amazonaws.services.s3.AmazonS3;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MvcResult;

public class BaseControllerWithAmazonS3ApiTest extends BaseControllerApiTest {

  @Autowired
  private AmazonS3 amazonS3;

  @Value("${amazon.bucket.name}")
  private String bucketName;

  protected void deleteFileFromBucket(MvcResult mvcResult, String dir)
      throws UnsupportedEncodingException {
    List<String> responseContent = Arrays.asList(
        mvcResult.getResponse().getContentAsString().split("\\s*,\\s*"));
    responseContent.forEach(s -> {
      String escapedString = s.replace("\"", "").replace("[", "").replace("]", "");
      String filePathToRemove = escapedString.substring(escapedString.lastIndexOf("/"));
      amazonS3.deleteObject(bucketName, dir + filePathToRemove);
    });

  }
}
