package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.model.AtlasListUtil;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.user.administration.mapper.GraphApiUserMapper;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder.GetRequestConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphApiService {

  private static final int SEARCH_QUERY_LIMIT = 10;
  private static final int RESOLVE_CHUNK_SIZE = 20;
  private static final String[] USER_PROPERTIES_TO_SELECT = {"onPremisesSamAccountName", "surname", "givenName", "mail",
      "accountEnabled", "displayName"};

  private final GraphServiceClient graphClient;

  public List<UserModel> searchUsers(String searchQuery) {
    return getUsers(requestConfig -> {
      Objects.requireNonNull(requestConfig.queryParameters);
      Objects.requireNonNull(requestConfig.headers);

      requestConfig.queryParameters.select = USER_PROPERTIES_TO_SELECT;
      requestConfig.queryParameters.top = SEARCH_QUERY_LIMIT;
      requestConfig.queryParameters.search = """
          "onPremisesSamAccountName:%s" OR "mail:%s" OR "displayName:%s"
          """.formatted(searchQuery, searchQuery, searchQuery);
      requestConfig.headers.add("ConsistencyLevel", "eventual");
    });
  }

  @Redacted
  public List<UserModel> resolveUsers(List<String> userIds) {
    List<UserModel> result = new ArrayList<>();
    AtlasListUtil.getPartitionedSublists(userIds, RESOLVE_CHUNK_SIZE)
        .forEach(sublist -> result.addAll(resolveUsersBatch(sublist)));
    return result;
  }

  private List<UserModel> resolveUsersBatch(List<String> userIds) {
    List<UserModel> resolvedUsers = resolveUsersViaGraphApi(userIds);

    List<UserModel> result = new ArrayList<>();
    userIds.forEach(userId -> {
      Optional<UserModel> resolvedUser = resolvedUsers.stream().filter(i -> i.getSbbUserId().equals(userId)).findFirst();
      if (resolvedUser.isPresent()) {
        result.add(resolvedUser.get());
      } else {
        result.add(UserModel.builder().sbbUserId(userId).accountStatus(UserAccountStatus.DELETED).build());
      }
    });

    return result;
  }

  private List<UserModel> resolveUsersViaGraphApi(List<String> userIds) {
    return getUsers(requestConfig -> {
      Objects.requireNonNull(requestConfig.queryParameters);
      Objects.requireNonNull(requestConfig.headers);

      requestConfig.queryParameters.select = USER_PROPERTIES_TO_SELECT;
      requestConfig.queryParameters.filter = "onPremisesSamAccountName in (%s)".formatted(
          userIds.stream().map("'%s'"::formatted).collect(Collectors.joining(", ")));
      requestConfig.queryParameters.count = true;
      requestConfig.headers.add("ConsistencyLevel", "eventual");
    });
  }

  private List<UserModel> getUsers(Consumer<GetRequestConfiguration> requestConfiguration) {
    UserCollectionResponse response = Objects.requireNonNull(graphClient.users().get(requestConfiguration));
    List<User> users = Objects.requireNonNull(response.getValue());
    return users.stream().map(GraphApiUserMapper::userToModel).toList();
  }
}
