package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.model.AtlasListUtil;
import ch.sbb.atlas.user.administration.mapper.UserMapper;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import com.microsoft.graph.requests.UserCollectionRequest;
import com.microsoft.graph.requests.UserCollectionResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphApiService {

  private final GraphServiceClient<Request> graphClient;
  private final IGraphApiBatchRequestService graphApiBatchRequestService;

  private static final String USER_MODEL_SELECTED_PROPERTIES = """
      onPremisesSamAccountName,surname,givenName,mail,accountEnabled,displayName
      """;
  private static final String FILTER_PREFIX = "$filter";
  private static final String SEARCH_PREFIX = "$search";
  private static final String FILTER_EQ_QUERY = "%s eq '%s'";
  private static final String SEARCH_QUERY = "\"%s:%s\"";
  private static final String SBB_USER_ID_PROP = "onPremisesSamAccountName";
  private static final String DISPLAY_NAME_PROP = "displayName";
  private static final String MAIL_PROP = "mail";
  private static final byte SEARCH_QUERY_LIMIT = 10;
  public static final byte BATCH_REQUEST_LIMIT = 20;

  // returns first 10 elements found by displayName + first 10 elements found by mail (distinct)
  public List<UserModel> searchUsers(String searchQuery) {
    // by displayName contains
    UserCollectionPage usersByDisplayName =
        getUserSearchRequest(buildSearchQueryOption(DISPLAY_NAME_PROP, searchQuery)).top(SEARCH_QUERY_LIMIT).get();
    // by mail startswith
    UserCollectionPage usersByMail =
        getUserSearchRequest(buildSearchQueryOption(MAIL_PROP, searchQuery)).top(SEARCH_QUERY_LIMIT).get();
    // by userId startswith
    UserCollectionPage usersByUserId =
        getUserSearchRequest(buildSearchQueryOption(SBB_USER_ID_PROP, searchQuery)).top(SEARCH_QUERY_LIMIT).get();

    List<UserModel> userResult = getUserModelsFromUserCollectionPages(usersByDisplayName, usersByMail, usersByUserId);
    return userResult.stream().distinct().toList();
  }

  public List<UserModel> resolveUsers(@NonNull List<String> userIds) {
    List<UserModel> result = new ArrayList<>();
    AtlasListUtil.getPartitionedSublists(userIds, BATCH_REQUEST_LIMIT)
        .forEach(sublist -> result.addAll(resolveUsersBatch(sublist)));
    return result;
  }

  private List<UserModel> resolveUsersBatch(List<String> userIds) {
    List<String> requestIds = new ArrayList<>();
    BatchRequestContent batchRequestContent = new BatchRequestContent();
    userIds.forEach(
        userId -> requestIds.add(graphApiBatchRequestService.addBatchRequest(batchRequestContent,
            getUserSearchRequest(buildFilterQueryOption(SBB_USER_ID_PROP, userId)).count(true)
        )));
    BatchResponseContent batchResponseContent = graphApiBatchRequestService.sendBatchRequest(
        batchRequestContent);

    List<UserModel> result = new ArrayList<>();
    for (int i = 0; i < requestIds.size(); i++) {
      UserCollectionResponse deserializedBody = graphApiBatchRequestService.getDeserializedBody(
          batchResponseContent,
          UserCollectionResponse.class, requestIds.get(i));
      if (deserializedBody == null || deserializedBody.value == null || deserializedBody.value.size() != 1) {
        result.add(UserModel.builder()
            .sbbUserId(userIds.get(i))
            .accountStatus(UserAccountStatus.DELETED)
            .build());
      } else {
        User user = deserializedBody.value.get(0);
        result.add(UserMapper.userToModel(user));
      }
    }
    return result;
  }

  private QueryOption buildSearchQueryOption(String searchProperty, String searchQuery) {
    return new QueryOption(SEARCH_PREFIX, SEARCH_QUERY.formatted(searchProperty, searchQuery));
  }

  private QueryOption buildFilterQueryOption(String filterProperty, String filterQuery) {
    return new QueryOption(FILTER_PREFIX, FILTER_EQ_QUERY.formatted(filterProperty, filterQuery));
  }

  private UserCollectionRequest getDefaultUserCollectionRequest(LinkedList<Option> requestOptions) {
    return graphClient.users()
        .buildRequest(requestOptions)
        .select(USER_MODEL_SELECTED_PROPERTIES);
  }

  private LinkedList<Option> getRequestOptionsWithConsistencyLevel(Option... options) {
    LinkedList<Option> requestOptions = new LinkedList<>(
        List.of(new HeaderOption("ConsistencyLevel", "eventual")));
    requestOptions.addAll(Arrays.asList(options));
    return requestOptions;
  }

  private UserCollectionRequest getUserSearchRequest(QueryOption... queryOptions) {
    return getDefaultUserCollectionRequest(getRequestOptionsWithConsistencyLevel(queryOptions));
  }

  private List<UserModel> getUserModelsFromUserCollectionPages(
      UserCollectionPage... userCollectionPages) {
    return Arrays.stream(userCollectionPages)
        .flatMap(page -> page.getCurrentPage().stream().map(UserMapper::userToModel))
        .toList();
  }

}
