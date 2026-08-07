package ch.sbb.atlas.user.administration.module.userinformation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.UserModel;
import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import com.microsoft.graph.users.UsersRequestBuilder.GetRequestConfiguration;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class GraphApiServiceTest {

  @Mock
  private GraphServiceClient graphClient;

  @Mock
  private UsersRequestBuilder usersRequestBuilder;

  @Mock
  private UserCollectionResponse userCollectionResponse;

  @Captor
  private ArgumentCaptor<Consumer<GetRequestConfiguration>> getRequestConfigCaptor;

  private GraphApiService graphApiService;

  private AutoCloseable mockCloseable;

  @BeforeEach
  void setUp() {
    mockCloseable = MockitoAnnotations.openMocks(this);
    graphApiService = new GraphApiService(graphClient);
    // @Value is only populated by Spring; set it explicitly here so the pool-size logic under
    // test behaves as it would at runtime instead of always collapsing to a single thread.
    ReflectionTestUtils.setField(graphApiService, "graphParallelism", 8);

    when(graphClient.users()).thenReturn(usersRequestBuilder);
    when(usersRequestBuilder.get(any())).thenReturn(userCollectionResponse);
  }

  @AfterEach
  void tearDown() throws Exception {
    mockCloseable.close();
  }

  @Test
  void shouldSearchUsers() {
    graphApiService.searchUsers("user1");

    GetRequestConfiguration configuration = verifyGetAndReturnConfiguration();

    String expectedSearchFilter = """
        "onPremisesSamAccountName:user1" OR "mail:user1" OR "displayName:user1" OR "userPrincipalName:user1"
        """;
    assertThat(configuration.queryParameters).isNotNull();
    assertThat(configuration.queryParameters.search).isEqualTo(expectedSearchFilter);
    assertThat(configuration.queryParameters.top).isEqualTo(10);

    assertThat(configuration.headers)
        .isNotNull()
        .hasSize(1)
        .containsKey("ConsistencyLevel");
  }

  @Test
  void shouldSearchUserByMail() {
    graphApiService.searchUserByMail("test@test.com");

    GetRequestConfiguration configuration = verifyGetAndReturnConfiguration();

    String expectedSearchFilter = """
        "mail:test@test.com" OR "userPrincipalName:test@test.com"
        """;
    assertThat(configuration.queryParameters).isNotNull();
    assertThat(configuration.queryParameters.search).isEqualTo(expectedSearchFilter);
    assertThat(configuration.queryParameters.top).isEqualTo(10);

    assertThat(configuration.headers)
        .isNotNull()
        .hasSize(1)
        .containsKey("ConsistencyLevel");
  }

  @Test
  void shouldResolveUsers() {
    graphApiService.resolveUsers(List.of("user1", "user2"));

    GetRequestConfiguration configuration = verifyGetAndReturnConfiguration();

    assertThat(configuration.queryParameters).isNotNull();
    assertThat(configuration.queryParameters.filter).isEqualTo("onPremisesSamAccountName in ('user1', 'user2')");
    assertThat(configuration.queryParameters.count).isTrue();

    assertThat(configuration.headers)
        .isNotNull()
        .hasSize(1)
        .containsKey("ConsistencyLevel");
  }

  @Test
  void shouldResolveAllUsersWhenBatchedInParallel() {
    // given: more ids than a single Graph resolve batch (RESOLVE_CHUNK_SIZE = 20), forcing
    // resolveUsers to split them across multiple concurrent Graph API calls
    List<String> userIds = IntStream.range(0, 25).mapToObj("user%d"::formatted).toList();

    // when
    List<UserModel> result = graphApiService.resolveUsers(userIds);

    // then: every requested id comes back exactly once, and at least two Graph calls were made
    assertThat(result).extracting(UserModel::getSbbUserId).containsExactlyInAnyOrderElementsOf(userIds);
    verify(graphClient, atLeast(2)).users();
  }

  @Test
  void shouldResolveBatchesConcurrentlyWhenMultipleBatchesArePending() throws Exception {
    // given: two batches (25 ids, RESOLVE_CHUNK_SIZE = 20) and a Graph stub that only returns
    // once both batch calls are in flight at the same time. A sequential implementation would
    // block on the first call forever (nobody left to release the barrier), so this only
    // completes if resolveUsers dispatches both batches concurrently.
    List<String> userIds = IntStream.range(0, 25).mapToObj("user%d"::formatted).toList();
    CyclicBarrier bothBatchesInFlight = new CyclicBarrier(2);
    when(usersRequestBuilder.get(any())).thenAnswer(invocation -> {
      bothBatchesInFlight.await(2, TimeUnit.SECONDS);
      return userCollectionResponse;
    });

    // when
    List<UserModel> result = graphApiService.resolveUsers(userIds);

    // then
    assertThat(result).extracting(UserModel::getSbbUserId).containsExactlyInAnyOrderElementsOf(userIds);
  }

  @Test
  void shouldReturnEmptyListWhenResolvingNoUsers() {
    // when
    List<UserModel> result = graphApiService.resolveUsers(List.of());

    // then
    assertThat(result).isEmpty();
  }

  private GetRequestConfiguration verifyGetAndReturnConfiguration() {
    verify(graphClient).users();
    verify(usersRequestBuilder).get(getRequestConfigCaptor.capture());

    GetRequestConfiguration getRequestConfiguration = usersRequestBuilder.new GetRequestConfiguration();
    Consumer<GetRequestConfiguration> requestConfig = getRequestConfigCaptor.getValue();
    requestConfig.accept(getRequestConfiguration);

    return getRequestConfiguration;
  }
}
