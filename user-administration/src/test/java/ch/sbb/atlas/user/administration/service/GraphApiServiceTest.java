package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.microsoft.graph.models.UserCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.UsersRequestBuilder;
import com.microsoft.graph.users.UsersRequestBuilder.GetRequestConfiguration;
import java.util.List;
import java.util.function.Consumer;
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

  private GraphApiService graphApiService;

  @Captor
  private ArgumentCaptor<Consumer<GetRequestConfiguration>> getRequestConfigCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    graphApiService = new GraphApiService(graphClient);

    when(graphClient.users()).thenReturn(usersRequestBuilder);
    when(usersRequestBuilder.get(any())).thenReturn(userCollectionResponse);
  }

  @Test
  void shouldSearchUsers() {
    graphApiService.searchUsers("U236171");

    GetRequestConfiguration configuration = verifyGetAndReturnConfiguration();

    String expectedSearchFilter = """
        "onPremisesSamAccountName:U236171" OR "mail:U236171" OR "displayName:U236171"
        """;
    assertThat(configuration.queryParameters).isNotNull();
    assertThat(configuration.queryParameters.search).isEqualTo(expectedSearchFilter);
    assertThat(configuration.queryParameters.top).isEqualTo(10);

    assertThat(configuration.headers).isNotNull();
    assertThat(configuration.headers).hasSize(1);
    assertThat(configuration.headers).containsKey("ConsistencyLevel");
  }

  @Test
  void shouldResolveUsers() {
    graphApiService.resolveUsers(List.of("U236171", "e502999"));

    GetRequestConfiguration configuration = verifyGetAndReturnConfiguration();

    assertThat(configuration.queryParameters).isNotNull();
    assertThat(configuration.queryParameters.filter).isEqualTo("onPremisesSamAccountName in ('U236171', 'e502999')");
    assertThat(configuration.queryParameters.count).isEqualTo(true);

    assertThat(configuration.headers).isNotNull();
    assertThat(configuration.headers).hasSize(1);
    assertThat(configuration.headers).containsKey("ConsistencyLevel");
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
