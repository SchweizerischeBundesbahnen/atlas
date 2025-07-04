package ch.sbb.atlas.user.administration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.repository.ClientCredentialPermissionRepository;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka(topics = {"atlas.user.administration"})
class ClientCredentialAdministrationControllerApiTest extends BaseControllerApiTest {

  static final String CLIENT_ID = "1323165464-46465546-14364";
  static final String ALIAS = "öV-info.ch";

  @Autowired
  private ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  @AfterEach
  void cleanup() {
    clientCredentialPermissionRepository.deleteAll();
  }

  @Test
  void shouldGetClient() throws Exception {
    ClientCredentialPermission clientCredentialPermission = ClientCredentialPermission.builder()
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.TTFN)
        .clientCredentialId(CLIENT_ID)
        .alias(ALIAS)
        .build();
    clientCredentialPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .clientCredentialPermission(clientCredentialPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction("ch:1:sboid:123123")
        .build()));
    clientCredentialPermissionRepository.save(clientCredentialPermission);
    clientCredentialPermission =
        ClientCredentialPermission.builder()
            .role(ApplicationRole.WRITER)
            .application(ApplicationType.TIMETABLE_HEARING)
            .clientCredentialId(CLIENT_ID)
            .alias(ALIAS)
            .build();
    clientCredentialPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .clientCredentialPermission(clientCredentialPermission)
        .type(PermissionRestrictionType.CANTON)
        .restriction(SwissCanton.BERN.name())
        .build()));
    clientCredentialPermissionRepository.save(clientCredentialPermission);

    mvc.perform(get("/v1/client-credentials/" + CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientCredentialId").value(CLIENT_ID))
        .andExpect(jsonPath("$.permissions", hasSize(2)))
        .andExpect(jsonPath("$.permissions[0].permissionRestrictions", hasSize(1)))
        .andExpect(jsonPath("$.permissions[1].permissionRestrictions", hasSize(1)));
  }

  @Test
  void shouldGetClientCredentialOverview() throws Exception {
    ClientCredentialPermission clientCredentialPermission = ClientCredentialPermission.builder()
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.TTFN)
        .clientCredentialId(CLIENT_ID)
        .alias(ALIAS)
        .build();
    clientCredentialPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .clientCredentialPermission(clientCredentialPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction("ch:1:sboid:123123")
        .build()));
    clientCredentialPermissionRepository.save(clientCredentialPermission);
    clientCredentialPermission =
        ClientCredentialPermission.builder()
            .role(ApplicationRole.WRITER)
            .application(ApplicationType.TIMETABLE_HEARING)
            .clientCredentialId(CLIENT_ID)
            .alias(ALIAS)
            .build();
    clientCredentialPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .clientCredentialPermission(clientCredentialPermission)
        .type(PermissionRestrictionType.CANTON)
        .restriction(SwissCanton.BERN.name())
        .build()));
    clientCredentialPermissionRepository.save(clientCredentialPermission);

    mvc.perform(get("/v1/client-credentials"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.objects", hasSize(1)));
  }

  @Test
  void shouldCreateClient() throws Exception {
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
        .clientCredentialId("new-client-id")
        .alias("some-alias")
        .comment("Comment")
        .build();

    mvc.perform(post("/v1/client-credentials").content(mapper.writeValueAsString(client)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.clientCredentialId").value("new-client-id"));
  }

  @Test
  void shouldUpdateClientPermissions() throws Exception {
    // given
    ClientCredentialPermission clientCredentialPermission = ClientCredentialPermission.builder()
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.TTFN)
        .clientCredentialId(CLIENT_ID)
        .alias(ALIAS)
        .build();
    clientCredentialPermission.setPermissionRestrictions(Set.of(PermissionRestriction.builder()
        .clientCredentialPermission(clientCredentialPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction("ch:1:sboid:123123")
        .build()));
    clientCredentialPermissionRepository.save(clientCredentialPermission);

    // when
    PermissionModel permission = PermissionModel.builder()
        .role(ApplicationRole.READER)
        .application(ApplicationType.TTFN)
        .permissionRestrictions(Collections.emptyList())
        .build();

    mvc.perform(put("/v1/client-credentials/"
            + CLIENT_ID + "/" + ApplicationType.TTFN).content(mapper.writeValueAsString(permission))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.clientCredentialId").value(CLIENT_ID))
        .andExpect(jsonPath("$.permissions", hasSize(1)))
        .andExpect(jsonPath("$.permissions[0].role").value("READER"));
  }

}
