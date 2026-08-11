package ch.sbb.atlas.user.administration.module.userinformation.service;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.model.AtlasListUtil;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.user.administration.module.userinformation.mapper.GraphApiUserMapper;
import com.microsoft.graph.models.User;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder.GetRequestConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GraphApiService {

  private static final int SEARCH_QUERY_LIMIT = 10;
  private static final int RESOLVE_CHUNK_SIZE = 20;
  private static final String[] USER_PROPERTIES_TO_SELECT = {"onPremisesSamAccountName", "surname", "givenName", "mail",
      "accountEnabled", "displayName", "userPrincipalName"};
  private static final String CONSISTENCY_LEVEL = "ConsistencyLevel";
  private static final String EVENTUAL = "eventual";

  private final GraphServiceClient graphClient;

  @Value("${atlas.user-administration.emails.graph-parallelism:8}")
  private int graphParallelism;

  public List<UserModel> searchUsers(String searchQuery) {
    return getUsers(requestConfig -> {
      Objects.requireNonNull(requestConfig.queryParameters);
      Objects.requireNonNull(requestConfig.headers);

      requestConfig.queryParameters.select = USER_PROPERTIES_TO_SELECT;
      requestConfig.queryParameters.top = SEARCH_QUERY_LIMIT;
      requestConfig.queryParameters.search = """
          "onPremisesSamAccountName:%s" OR "mail:%s" OR "displayName:%s" OR "userPrincipalName:%s"
          """.formatted(searchQuery, searchQuery, searchQuery, searchQuery);
      requestConfig.headers.add(CONSISTENCY_LEVEL, EVENTUAL);
    });
  }

  @Redacted
  public List<UserModel> searchUserByMail(String mail) {
    return getUsers(requestConfig -> {
      Objects.requireNonNull(requestConfig.queryParameters);
      Objects.requireNonNull(requestConfig.headers);

      requestConfig.queryParameters.select = USER_PROPERTIES_TO_SELECT;
      requestConfig.queryParameters.top = SEARCH_QUERY_LIMIT;
      requestConfig.queryParameters.search = """
          "mail:%s" OR "userPrincipalName:%s"
          """.formatted(mail, mail);
      requestConfig.headers.add(CONSISTENCY_LEVEL, EVENTUAL);
    });
  }

  /**
   * Resolves the Graph API batches in parallel, bounded by
   * {@code atlas.user-administration.emails.graph-parallelism}, to keep latency reasonable
   * regardless of how many users are requested.
   */
  @Redacted
  public List<UserModel> resolveUsers(List<String> userIds) {
    List<List<String>> batches = List.copyOf(AtlasListUtil.getPartitionedSublists(userIds, RESOLVE_CHUNK_SIZE));
    if (batches.isEmpty()) {
      return List.of();
    }

    List<UserModel> result = new ArrayList<>();
    int poolSize = Math.max(1, Math.min(graphParallelism, batches.size()));
    try (ExecutorService executorService = Executors.newFixedThreadPool(poolSize)) {
      List<Future<List<UserModel>>> futures = batches.stream()
          .map(batch -> executorService.submit(() -> resolveUsersBatch(batch)))
          .toList();

      for (Future<List<UserModel>> future : futures) {
        result.addAll(future.get());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while resolving users via Graph API", e);
    } catch (ExecutionException e) {
      throw new IllegalStateException("Failed to resolve users via Graph API", e.getCause());
    }
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
      requestConfig.headers.add(CONSISTENCY_LEVEL, EVENTUAL);
    });
  }

  private List<UserModel> getUsers(Consumer<GetRequestConfiguration> requestConfiguration) {
    UserCollectionResponse response = Objects.requireNonNull(graphClient.users().get(requestConfiguration));
    List<User> users = Objects.requireNonNull(response.getValue());
    return users.stream().map(GraphApiUserMapper::userToModel).toList();
  }
}
