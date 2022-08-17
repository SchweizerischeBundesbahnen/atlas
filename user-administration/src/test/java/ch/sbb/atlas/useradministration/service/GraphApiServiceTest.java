package ch.sbb.atlas.useradministration.service;

import ch.sbb.atlas.useradministration.models.UserModel;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import com.microsoft.graph.requests.UserCollectionRequest;
import com.microsoft.graph.requests.UserCollectionRequestBuilder;
import java.util.List;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class GraphApiServiceTest {

  private GraphApiService graphApiService;

  @Mock
  private GraphServiceClient<Request> graphServiceClientMock;

  @Mock
  private UserCollectionRequestBuilder userCollectionRequestBuilderMock;

  @Mock
  private UserCollectionRequest userCollectionRequestMock;

  @Mock
  private UserCollectionPage userCollectionPageMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    graphApiService = new GraphApiService(graphServiceClientMock);
  }

  @Test
  void shouldSearchUsersByDisplayNameAndMail() {
    doReturn(userCollectionRequestBuilderMock).when(graphServiceClientMock).users();
    doReturn(userCollectionRequestMock).when(userCollectionRequestBuilderMock).buildRequest(anyList());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).top(anyInt());
    doReturn(userCollectionRequestMock).when(userCollectionRequestMock).select(anyString());

    doReturn(userCollectionPageMock).when(userCollectionRequestMock).get();
    User user = new User();
    user.displayName = "test";

    doReturn(List.of(user)).when(userCollectionPageMock).getCurrentPage();

    List<UserModel> result = graphApiService.searchUsersByDisplayNameAndMail("test");
    System.out.println(result.toString());
  }

}
