package ch.sbb.line.directory.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import com.amazonaws.services.s3.AmazonS3;
import java.io.UnsupportedEncodingException;
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
    String result = mvcResult.getResponse().getContentAsString();
    String filePath = result.substring(result.lastIndexOf("/"), result.length() - 1);
    amazonS3.deleteObject(bucketName, dir + filePath);
  }
}
