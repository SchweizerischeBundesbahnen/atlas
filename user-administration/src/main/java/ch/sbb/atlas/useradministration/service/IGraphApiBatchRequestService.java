package ch.sbb.atlas.useradministration.service;

import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.http.IHttpRequest;

public interface IGraphApiBatchRequestService {

  String addBatchRequest(BatchRequestContent batchRequestContent, IHttpRequest request);

  BatchResponseContent sendBatchRequest(BatchRequestContent batchRequestContent);

  <T> T getDeserializedBody(BatchResponseContent batchResponseContent, Class<T> classType, String responseId);

}
