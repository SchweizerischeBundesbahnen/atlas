package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserInformationApiControllerTest extends BaseControllerApiTest {

    @MockBean
    private GraphApiService graphApiService;

    @MockBean
    private UserAdministrationService userAdministrationService;

    @Test
    void shoudSearchUserInAD() throws Exception {
        UserModel userModel = UserModel.builder()
                        .sbbUserId("u239096")
                        .firstName("daniel")
                        .lastName("djukic")
                        .mail("daniel.djukic@sbb.ch")
                        .accountStatus(UserAccountStatus.ACTIVE)
                        .build();

        UserModel userModel2 = UserModel.builder()
                .sbbUserId("u239097")
                .firstName("hans")
                .lastName("müller")
                .mail("hans.müller@sbb.ch")
                .accountStatus(UserAccountStatus.ACTIVE)
                .build();


        when(graphApiService.searchUsers("testQuery")).thenReturn(List.of(userModel, userModel2));

        mvc.perform(get("/v1/search")
                .param("searchQuery", "testQuery"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(status().isOk());

        verify(graphApiService, times(1)).searchUsers("testQuery");
        verifyNoInteractions(userAdministrationService);
    }

    @Test
    void shouldSearchUserInAtlas() throws Exception {
        UserModel userModel = UserModel.builder()
                .sbbUserId("u239096")
                .firstName("daniel")
                .lastName("djukic")
                .mail("daniel.djukic@sbb.ch")
                .accountStatus(UserAccountStatus.ACTIVE)
                .build();

        UserModel userModel2 = UserModel.builder()
                .sbbUserId("u239097")
                .firstName("hans")
                .lastName("müller")
                .mail("hans.müller@sbb.ch")
                .accountStatus(UserAccountStatus.ACTIVE)
                .build();

        List<UserModel> userModels = Arrays.asList(userModel, userModel2);
        when(graphApiService.searchUsers("testQuery")).thenReturn(userModels);

        List<UserModel> filteredUsers = Collections.singletonList(userModel);

        when(userAdministrationService.filterForUserInAtlas(userModels, ApplicationType.SEPODI))
                .thenReturn(filteredUsers);


        mvc.perform(get("/v1/search-in-atlas")
                        .param("searchQuery", "testQuery")
                        .param("applicationType", "SEPODI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sbbUserId").value("u239096"));

        verify(graphApiService, times(1)).searchUsers("testQuery");
        verify(userAdministrationService, times(1)).filterForUserInAtlas(userModels, ApplicationType.SEPODI);

    }
}