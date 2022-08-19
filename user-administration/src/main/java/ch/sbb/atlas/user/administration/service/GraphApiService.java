package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.user.administration.models.UserModel;
import ch.sbb.atlas.user.administration.enums.UserAccountStatus;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphApiService {

  private final GraphServiceClient<Request> graphClient;
  private final IGraphApiBatchRequestService graphApiBatchRequestService;

  private static final String USER_MODEL_SELECTED_PROPERTIES = "onPremisesSamAccountName,surname,givenName,mail,accountEnabled,displayName";
  private static final String FILTER_PREFIX = "$filter";
  private static final String SEARCH_PREFIX = "$search";
  private static final String FILTER_EQ_QUERY = "%s eq '%s'";
  private static final String SEARCH_QUERY = "\"%s:%s\"";
  private static final String SBB_USER_ID_PROP = "onPremisesSamAccountName";
  private static final String DISPLAY_NAME_PROP = "displayName";
  private static final String MAIL_PROP = "mail";

  private static final byte SEARCH_QUERY_LIMIT = 10;

  // returns first 10 elements found by displayName + first 10 elements found by mail (distinct)
  public List<UserModel> searchUsersByDisplayNameAndMail(String searchQuery) {
    // by displayName contains
    final Optional<UserCollectionPage> usersByDisplayName = Optional.ofNullable(
        getDefaultUserCollectionRequest(
            getRequestOptionsWithConsistencyLevel(
                new QueryOption(SEARCH_PREFIX,
                    SEARCH_QUERY.formatted(DISPLAY_NAME_PROP, searchQuery))
            )).top(SEARCH_QUERY_LIMIT).get()
    );
    // by mail startswith
    final Optional<UserCollectionPage> usersByMail = Optional.ofNullable(
        getDefaultUserCollectionRequest(
            getRequestOptionsWithConsistencyLevel(
                new QueryOption(SEARCH_PREFIX, SEARCH_QUERY.formatted(MAIL_PROP, searchQuery))
            )).top(SEARCH_QUERY_LIMIT).get()
    );

    final List<UserModel> userResult = new ArrayList<>(
        getUserModelsFromUserCollectionPage(usersByDisplayName));
    userResult.addAll(getUserModelsFromUserCollectionPage(usersByMail));
    return userResult.stream().distinct().toList();
  }

  private List<UserModel> getUserModelsFromUserCollectionPage(
      Optional<UserCollectionPage> userCollectionPage) {
    List<UserModel> userModels = new ArrayList<>();
    userCollectionPage.ifPresent(
        (collectionPage) -> userModels.addAll(collectionPage.getCurrentPage()
                                                            .stream()
                                                            .map((user) -> new UserModel(
                                                                user.onPremisesSamAccountName,
                                                                user.surname, user.givenName,
                                                                user.mail,
                                                                UserModel.getUserAccountStatusFromBoolean(
                                                                    user.accountEnabled),
                                                                user.displayName))
                                                            .toList()));
    return userModels;
  }

  public List<UserModel> resolveLdapUserDataFromUserIds(List<String> userIds) {
    userIds = userIds.stream().limit(20).toList();
    final List<String> requestIds = new ArrayList<>();
    final BatchRequestContent batchRequestContent = new BatchRequestContent();
    userIds.forEach((userId) -> {
      QueryOption filterUserOption = new QueryOption(FILTER_PREFIX,
          FILTER_EQ_QUERY.formatted(SBB_USER_ID_PROP, userId));
      requestIds.add(graphApiBatchRequestService.addBatchRequest(batchRequestContent,
          getDefaultUserCollectionRequest(getRequestOptionsWithConsistencyLevel(filterUserOption))
              .count(true)
      ));
    });
    BatchResponseContent batchResponseContent = graphApiBatchRequestService.sendBatchRequest(
        batchRequestContent);
    final List<UserModel> result = new ArrayList<>();
    for (int i = 0; i < requestIds.size(); i++) {
      final UserCollectionResponse deserializedBody = graphApiBatchRequestService.getDeserializedBody(batchResponseContent,
          UserCollectionResponse.class, requestIds.get(i));
      if (deserializedBody == null || deserializedBody.value == null
          || deserializedBody.value.size() != 1) {
        result.add(
            new UserModel(userIds.get(i), null, null, null, UserAccountStatus.DELETED, null));
        continue;
      }
      final User user = deserializedBody.value.get(0);
      result.add(
          new UserModel(user.onPremisesSamAccountName, user.surname, user.givenName,
              user.mail, UserModel.getUserAccountStatusFromBoolean(user.accountEnabled),
              user.displayName)
      );
    }
    return result;
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

}
