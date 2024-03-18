package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class GraphApiServiceIntegrationTest {

  @Autowired
  private GraphApiService graphApiService;

  @Test
  void shouldGetSearchResultsFromAzureAdSearchingSbbUid() {
    List<UserModel> searchResult = graphApiService.searchUsers("U236171");

    assertThat(searchResult).hasSize(1);
  }

  @Test
  void shouldGetSearchResultsFromAzureAdSearchingDisplayName() {
    List<UserModel> searchResult = graphApiService.searchUsers("luca ammann");

    assertThat(searchResult).hasSize(1);
  }

  @Test
  void shouldGetSearchResultsFromAzureAdSearchingMail() {
    List<UserModel> searchResult = graphApiService.searchUsers("luca.ammann2@sbb.ch");

    assertThat(searchResult).hasSize(1);
  }

  @Test
  void shouldResolveUserIds() {
    List<UserModel> searchResult = graphApiService.resolveUsers(
        List.of("e502999", "e504928", "e507826", "e510586", "e510738", "e510750", "e515951", "e516095", "e516161", "e516457",
            "e516953", "e517019", "e520628", "e521961", "e522897", "e522898", "e522899", "e522901", "e522903", "e522904",
            "e522908", "e522911", "e522912", "e522913", "e522920", "e522922", "e522924"));

    assertThat(searchResult).hasSize(27);
  }

  @Test
  void shouldResolveUserIdsWithOneUser() {
    List<UserModel> searchResult = graphApiService.resolveUsers(List.of("e502999"));

    assertThat(searchResult).hasSize(1);
  }
}
