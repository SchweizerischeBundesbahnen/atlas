package ch.sbb.atlas;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;
import java.util.LinkedList;
import java.util.List;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

@SpringBootApplication
public class LdapTest implements CommandLineRunner {

  @Value("${azure-graph-api.client-secret}")
  private String clientSecret;
  private static final Logger LOG = LoggerFactory.getLogger(LdapTest.class);
  private static final String SCOPE_GRAPH_API = "https://graph.microsoft.com/.default";

  public static void main(String[] args) {
    SpringApplication.run(LdapTest.class, args);
  }

  @Override
  public void run(String... args) {
    final GraphServiceClient<Request> graphClient = initializeGraphServiceClient();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    getGraphContains(graphClient);
    stopWatch.stop();
    LOG.info("time search: {}", stopWatch.getTotalTimeSeconds());

    stopWatch = new StopWatch();
    stopWatch.start();
    searchWithSbbUid(graphClient);
    stopWatch.stop();
    LOG.info("time sbbuid: {}", stopWatch.getTotalTimeSeconds());
  }

  private GraphServiceClient<Request> initializeGraphServiceClient() {
    final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
        .clientId("87e6e634-6ba1-4e7a-869d-3348b4c3eafc")
        .clientSecret(clientSecret)
        .tenantId("2cda5d11-f0ac-46b3-967d-af1b2e1bd01a")
        .build();
    final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(
        List.of(SCOPE_GRAPH_API), clientSecretCredential);
    return GraphServiceClient
        .builder()
        .authenticationProvider(tokenCredentialAuthProvider)
        .buildClient();
  }

  private void getGraphContains(GraphServiceClient<Request> graphClient) {
    LinkedList<Option> requestOptions = new LinkedList<Option>();
    requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
    // contains
//    requestOptions.add(new QueryOption("$search", "\"displayName:pascal\""));
    // => filter startswith (when prop is not displayName or description)
    requestOptions.add(new QueryOption("$search", "\"mail:pascal\""));

    final UserCollectionPage users = graphClient.users().buildRequest(requestOptions)
                                                .count(true).get();

    LOG.info(users.getCount().toString());

    final List<User> firstPage = users.getCurrentPage();

    firstPage.forEach((user) ->
        LOG.info("""
                Attributes for {}:
                  displayName: {}
                  userPrincipalName: {}
                  givenName: {}
                  id: {}
                  mail: {}
                  surname: {}
                """, user.userPrincipalName, user.displayName, user.userPrincipalName,
            user.givenName, user.id, user.mail, user.surname));
  }

  private void searchWithSbbUid(GraphServiceClient<Request> graphClient) {
    LinkedList<Option> requestOptions = new LinkedList<Option>();
    requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));

    final UserCollectionPage users = graphClient.users().buildRequest(requestOptions)
                                                .filter("onPremisesSamAccountName eq 'u236171'")
                                                .select(
                                                    "displayName,onPremisesSamAccountName,userPrincipalName")
                                                .count(true).get();

    LOG.info(users.getCount().toString());

    final List<User> firstPage = users.getCurrentPage();

    firstPage.forEach((user) ->
        LOG.info("""
                Attributes for {}:
                  displayName: {}
                  userPrincipalName: {}
                  onPremisesSamAccountName: {}
                """, user.userPrincipalName, user.displayName, user.userPrincipalName,
            user.onPremisesSamAccountName));
  }
}
