package ch.sbb.atlas.user.administration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.enumeration.ApplicationRole;
import ch.sbb.atlas.user.administration.enumeration.ApplicationType;
import ch.sbb.atlas.user.administration.models.UserModel.Fields;
import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import ch.sbb.atlas.user.administration.service.UserPermissionDistributor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

public class UserAdministrationControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private UserPermissionDistributor userPermissionDistributor;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private UserPermissionRepository userPermissionRepository;

    @AfterEach
    void cleanup() {
        userPermissionRepository.deleteAll();
    }

    @Test
    void shouldGetUsers() throws Exception {
        userPermissionRepository.saveAll(List.of(
                UserPermission.builder()
                        .role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.TTFN)
                        .sbbUserId("u236171").build(),
                UserPermission.builder()
                        .role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.TTFN)
                        .sbbUserId("e999999").build(),
                UserPermission.builder()
                        .role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.LIDI)
                        .sbbUserId("u236171").build()
        ));

        mvc.perform(get("/v1/users")
                        .queryParam("page", "0")
                        .queryParam("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2))
                .andExpect(jsonPath("$.objects", hasSize(2)))
                .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'e999999')].accountStatus").value("DELETED"))
                .andExpect(
                        jsonPath("$.objects[?(@.sbbUserId == 'e999999')].permissions[0].role").value("SUPERVISOR"))
                .andExpect(
                        jsonPath("$.objects[?(@.sbbUserId == 'e999999')].permissions[0].application").value(
                                "TTFN"))
                .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u236171')].accountStatus").value("ACTIVE"))
                .andExpect(
                        jsonPath("$.objects[?(@.sbbUserId == 'u236171')].permissions[*]").value(hasSize(2)))
                .andExpect(jsonPath("$.objects[?(@.sbbUserId == 'u236171')].lastName").value("Ammann"));
    }

    @Test
    void shouldGetUser() throws Exception {
        userPermissionRepository.saveAll(List.of(
                UserPermission.builder()
                        .role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.TTFN)
                        .sbbUserId("u236171").build(),
                UserPermission.builder()
                        .role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.TTFN)
                        .sbbUserId("e678574").build()
        ));

        mvc.perform(get("/v1/users/U236171"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sbbUserId").value("u236171"))
                .andExpect(jsonPath("$.lastName").value("Ammann"))
                .andExpect(jsonPath("$.permissions").value(hasSize(1)))
                .andExpect(jsonPath("$.permissions[0].role").value("SUPERVISOR"))
                .andExpect(jsonPath("$.permissions[0].application").value("TTFN"));
    }

    @Test
    void shouldThrowPageSizeException() throws Exception {
        mvc.perform(get("/v1/users")
                        .queryParam("page", "0")
                        .queryParam("size", "21"))
                .andExpect(status().is(405))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value("Page size 21 is bigger than max allowed page size 20"))
                .andExpect(jsonPath("$.error").value("Page Request not allowed"))
                .andExpect(jsonPath("$.details").value(hasSize(0)));
    }

    @Test
    void shouldCreateUserPermission() throws Exception {
        UserPermissionModel userPermissionModelWriter = UserPermissionModel.builder()
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.TTFN)
                .sboids(List.of("ch:1:sboid:test"))
                .build();
        UserPermissionModel userPermissionModelReader = UserPermissionModel.builder()
                .role(ApplicationRole.READER)
                .application(ApplicationType.BODI)
                .sboids(List.of())
                .build();
        UserPermissionCreateModel model = UserPermissionCreateModel
                .builder()
                .sbbUserId("U236171")
                .permissions(List.of(
                        userPermissionModelWriter, userPermissionModelReader
                ))
                .build();

        mvc.perform(post("/v1/users")
                        .content(mapper.writeValueAsString(model)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + Fields.sbbUserId).value("u236171"))
                .andExpect(jsonPath("$." + Fields.mail).value("luca.ammann2@sbb.ch"))
                .andExpect(jsonPath("$." + Fields.permissions).value(hasSize(2)))
                .andExpect(jsonPath("$." + Fields.permissions + "[?(@.application == 'BODI')].sboids[*]").value(hasSize(0)))
                .andExpect(jsonPath("$." + Fields.permissions + "[?(@.application == 'TTFN')].sboids[*]").value(hasItem("ch:1:sboid:test")));

        List<UserPermission> savedPermissions = userPermissionRepository.findBySbbUserIdIgnoreCase("U236171");
        assertThat(savedPermissions).hasSize(2);
        assertThat(savedPermissions.get(0).getSbbUserId()).isEqualTo("u236171");
        assertThat(savedPermissions.get(1).getSbbUserId()).isEqualTo("u236171");
    }

    @Test
    void shouldThrowUserPermissionConflictExceptionOnCreateUserPermission() throws Exception {
        UserPermission userPermission = UserPermission.builder()
                .role(ApplicationRole.SUPERVISOR)
                .application(ApplicationType.TTFN)
                .sbbUserId("u236171").build();
        userPermissionRepository.save(userPermission);

        UserPermissionCreateModel createModel = UserPermissionCreateModel
                .builder()
                .sbbUserId("u236171")
                .permissions(List.of(
                        UserPermissionModel.builder()
                                .role(ApplicationRole.WRITER)
                                .application(ApplicationType.TTFN)
                                .sboids(List.of("ch:1:sboid:test"))
                                .build()
                ))
                .build();

        mvc.perform(post("/v1/users")
                        .content(mapper.writeValueAsString(createModel)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("A conflict occurred on UserPermission"))
                .andExpect(jsonPath("$.error").value("User Permission Conflict"))
                .andExpect(jsonPath("$.details").value(hasSize(1)));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        userPermissionRepository.save(UserPermission.builder()
                .role(ApplicationRole.SUPERVISOR)
                .application(ApplicationType.TTFN)
                .sbbUserId("u236171").build());

        UserPermissionCreateModel editedPermissions = UserPermissionCreateModel.builder()
                .sbbUserId("u236171")
                .permissions(List.of(
                        UserPermissionModel.builder()
                                .application(
                                        ApplicationType.TTFN)
                                .role(
                                        ApplicationRole.WRITER)
                                .sboids(
                                        List.of(
                                                "ch:1:sboid:10009"))
                                .build()))
                .build();

        mvc.perform(put("/v1/users").contentType(contentType)
                        .content(mapper.writeValueAsString(editedPermissions)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sbbUserId").value("u236171"))
                .andExpect(jsonPath("$.lastName").value("Ammann"))
                .andExpect(jsonPath("$.permissions").value(hasSize(1)))
                .andExpect(jsonPath("$.permissions[0].role").value("WRITER"))
                .andExpect(jsonPath("$.permissions[0].application").value("TTFN"));
    }

}
