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
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    // resolveUsersInParallel to split them across multiple concurrent Graph API calls
    List<String> userIds = IntStream.range(0, 25).mapToObj("user%d"::formatted).toList();

    // when
    List<UserModel> result = graphApiService.resolveUsersInParallel(userIds);

    // then: every requested id comes back exactly once, and at least two Graph calls were made
    assertThat(result).extracting(UserModel::getSbbUserId).containsExactlyInAnyOrderElementsOf(userIds);
    verify(graphClient, atLeast(2)).users();
  }

  @Test
  void shouldReturnEmptyListWhenResolvingNoUsersInParallel() {
    // when
    List<UserModel> result = graphApiService.resolveUsersInParallel(List.of());

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
