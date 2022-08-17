package ch.sbb.atlas.useradministration.service;

import ch.sbb.atlas.useradministration.models.UserModel;
import ch.sbb.atlas.useradministration.enums.UserAccountStatus;
import com.google.gson.JsonElement;
import com.microsoft.graph.content.BatchRequestContent;
import com.microsoft.graph.content.BatchResponseContent;
import com.microsoft.graph.content.BatchResponseStep;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import com.microsoft.graph.requests.UserCollectionResponse;
import java.util.ArrayList;
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

  // returns first 10 elements found by displayName + first 10 elements found by mail (distinct)
  public List<UserModel> searchUsersByDisplayNameAndMail(String searchQuery) {
    LinkedList<Option> requestOptions = new LinkedList<>();
    requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
    // displayName contains
    requestOptions.add(new QueryOption("$search", "\"displayName:" + searchQuery + "\""));

    final Optional<UserCollectionPage> usersByDisplayName = Optional.ofNullable(
        graphClient.users().buildRequest(requestOptions).top(10)
                   .select(
                       "onPremisesSamAccountName,surname,givenName,mail,accountEnabled,displayName")
                   .get());

    // => mail startswith (when prop is not displayName or description)
    requestOptions.set(1, new QueryOption("$search", "\"mail:" + searchQuery + "\""));

    final Optional<UserCollectionPage> usersByMail = Optional.ofNullable(
        graphClient.users().buildRequest(requestOptions).top(10)
                   .select(
                       "onPremisesSamAccountName,surname,givenName,mail,accountEnabled,displayName")
                   .get());

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

    final BatchRequestContent batchRequestContent = new BatchRequestContent();

    final List<String> requestIds = new ArrayList<>();

    LinkedList<Option> requestOptions = new LinkedList<>();
    requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
    requestOptions.add(new QueryOption("onPremisesSamAccountName", userIds.get(0)));

    userIds.forEach((userId) -> {
      requestOptions.set(1,
          new QueryOption("$filter", "onPremisesSamAccountName eq '" + userId + "'"));
      requestIds.add(batchRequestContent.addBatchRequestStep(
          graphClient.users()
                     .buildRequest(requestOptions)
                     .select(
                         "onPremisesSamAccountName,surname,givenName,mail,accountEnabled,displayName")
                     .count(true)
      ));
    });

    final BatchResponseContent batchResponseContent = graphClient.batch()
                                                                 .buildRequest()
                                                                 .post(batchRequestContent);

    final List<UserModel> result = new ArrayList<>();

    if (batchResponseContent == null) {
      return result;
    }

    for (int i = 0; i < requestIds.size(); i++) {
      final BatchResponseStep<JsonElement> response = batchResponseContent.getResponseById(
          requestIds.get(i));
      if (response == null) {
        continue;
      }
      final UserCollectionResponse deserializedBody = response.getDeserializedBody(
          UserCollectionResponse.class);
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

}
