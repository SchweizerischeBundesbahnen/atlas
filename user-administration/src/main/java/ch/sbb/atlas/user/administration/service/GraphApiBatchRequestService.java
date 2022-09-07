package ch.sbb.atlas.user.administration.service;

import com.google.gson.JsonElement;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.content.BatchResponseStep;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphApiBatchRequestService implements IGraphApiBatchRequestService {

  private final GraphServiceClient<Request> graphServiceClient;
  @Override
  public String addBatchRequest(BatchRequestContent batchRequestContent, IHttpRequest request) {
    return batchRequestContent.addBatchRequestStep(request);
  }

  @Override
  public BatchResponseContent sendBatchRequest(BatchRequestContent batchRequestContent){
    return graphServiceClient.batch().buildRequest().post(batchRequestContent);
  }

  @Override
  public <T> T getDeserializedBody(BatchResponseContent batchResponseContent, Class<T> classType, String responseId) {
    BatchResponseStep<JsonElement> response = batchResponseContent.getResponseById(responseId);
    if (response == null) {
      return null;
    }
    return response.getDeserializedBody(classType);
  }

}
