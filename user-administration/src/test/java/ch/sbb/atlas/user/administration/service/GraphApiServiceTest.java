package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import com.microsoft.graph.requests.UserCollectionRequest;
import com.microsoft.graph.requests.UserCollectionRequestBuilder;
import com.microsoft.graph.requests.UserCollectionResponse;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class GraphApiServiceTest {

  private GraphApiService graphApiService;

  @Mock
  private GraphServiceClient<Request> graphServiceClientMock;

  @Mock
  private UserCollectionRequestBuilder userCollectionRequestBuilderMock;

  @Mock
  private UserCollectionRequest userCollectionRequestMock;

  @Mock
  private UserCollectionPage userCollectionPageMock;

  @Mock
  private IGraphApiBatchRequestService graphApiBatchRequestService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    graphApiService = new GraphApiService(graphServiceClientMock, graphApiBatchRequestService);
  }

  @Test
  void shouldSearchUsersByDisplayNameAndMail() {
    doReturn(userCollectionRequestBuilderMock).when(graphServiceClientMock).users();
    doReturn(userCollectionRequestMock).when(userCollectionRequestBuilderMock)
                                       .buildRequest(anyList());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).top(anyInt());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).select(anyString());
    doReturn(userCollectionPageMock).when(userCollectionRequestMock).get();

    User user = new User();
    user.onPremisesSamAccountName = "u236171";
    user.accountEnabled = true;
    User user2 = new User();
    user2.onPremisesSamAccountName = "u123456";
    user2.accountEnabled = null;
    User user3 = new User();
    user3.onPremisesSamAccountName = "u654321";
    user3.accountEnabled = false;

    doReturn(List.of(user, user2, user3)).when(userCollectionPageMock).getCurrentPage();

    List<UserModel> result = graphApiService.searchUsers("test");

    verify(userCollectionRequestMock, times(3)).get();
    assertThat(result).hasSize(3);
    assertThat(result.get(0).getUserId()).isEqualTo("u236171");
    assertThat(result.get(0).getAccountStatus()).isEqualTo(UserAccountStatus.ACTIVE);

    assertThat(result.get(1).getUserId()).isEqualTo("u123456");
    assertThat(result.get(1).getAccountStatus()).isEqualTo(UserAccountStatus.INACTIVE);

    assertThat(result.get(2).getUserId()).isEqualTo("u654321");
    assertThat(result.get(2).getAccountStatus()).isEqualTo(UserAccountStatus.INACTIVE);
  }

  @Test
  void shouldResolveLdapUserDataFromUserIds() {
    List<String> userIds = new ArrayList<>();
    for (int i = 0; i < 18; i++) {
      userIds.add("u1234" + i);
    }

    doReturn(userCollectionRequestBuilderMock).when(graphServiceClientMock).users();
    doReturn(userCollectionRequestMock).when(userCollectionRequestBuilderMock)
                                       .buildRequest(anyList());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).select(anyString());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).count(true);

    doReturn("requestId").when(graphApiBatchRequestService).addBatchRequest(any(), any());

    User user = new User();
    user.onPremisesSamAccountName = "u236171";
    user.accountEnabled = true;

    UserCollectionResponseMockClass userCollectionPageMockClass = new UserCollectionResponseMockClass(
        List.of(user));

    doReturn(userCollectionPageMockClass).when(graphApiBatchRequestService)
                                         .getDeserializedBody(any(), any(), anyString());

    List<UserModel> userResult = graphApiService.resolveUsers(userIds);

    assertThat(userResult).hasSize(18);
    assertThat(userResult.get(0).getUserId()).isEqualTo("u236171");
    assertThat(userResult.get(0).getAccountStatus()).isEqualTo(UserAccountStatus.ACTIVE);

    doReturn(null).when(graphApiBatchRequestService).getDeserializedBody(any(), any(), anyString());

    userResult = graphApiService.resolveUsers(userIds);

    assertThat(userResult).hasSize(18);
    assertThat(userResult.get(0).getUserId()).isEqualTo("u12340");
    assertThat(userResult.get(0).getAccountStatus()).isEqualTo(UserAccountStatus.DELETED);
  }

  static class UserCollectionResponseMockClass extends UserCollectionResponse {
     UserCollectionResponseMockClass(List<User> value){
      super.value = value;
    }
  }

}
