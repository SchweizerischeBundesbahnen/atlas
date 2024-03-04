package ch.sbb.prm.directory.controller;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.prm.relation.RelationImportRequestModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ch.sbb.prm.directory.service.dataimport.RelationImportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RelationVersionControllerApiTest extends BaseControllerApiTest {

  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @MockBean
  private final RelationImportService relationImportService;

  @Autowired
  RelationVersionControllerApiTest(RelationRepository relationRepository, RelationImportService relationImportService, StopPointRepository stopPointRepository,
                                   SharedServicePointRepository sharedServicePointRepository) {
    this.relationRepository = relationRepository;
      this.relationImportService = relationImportService;
      this.stopPointRepository = stopPointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetRelationBySloid() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when & then
    mvc.perform(get("/v1/relations/" +relation1Sloid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].elementSloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsBySloidAndReferenceType() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when & then
    mvc.perform(get("/v1/relations/" +relation1Sloid + "/" + PLATFORM.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].elementSloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsByParentServicePointSloidAndReferenceType() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relation3 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    relation3.setValidFrom(LocalDate.of(2001, 1, 1));
    relation3.setValidTo(LocalDate.of(2001, 12, 31));
    relation3.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);
    relationRepository.saveAndFlush(relation3);

    //when & then
    mvc.perform(get("/v1/relations/parent-service-point-sloid/" + parentServicePointSloid + "/" + PLATFORM.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].elementSloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].parentServicePointSloid", is(parentServicePointSloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())))
        .andExpect(jsonPath("$[1].elementSloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[1].parentServicePointSloid", is(parentServicePointSloid)))
        .andExpect(jsonPath("$[1].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsByParentServicePointSloid() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relation3 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    relation3.setValidFrom(LocalDate.of(2001, 1, 1));
    relation3.setValidTo(LocalDate.of(2001, 12, 31));
    relation3.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);
    relationRepository.saveAndFlush(relation3);

    //when & then
    mvc.perform(get("/v1/relations/parent-service-point-sloid/" + parentServicePointSloid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateRelation() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    version1.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    version2.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersionModel editedVersionModel = new RelationVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setElementSloid(version2.getSloid());
    editedVersionModel.setReferencePointSloid(version2.getReferencePointSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setContrastingAreas(version2.getContrastingAreas());
    editedVersionModel.setTactileVisualMarks(version2.getTactileVisualMarks());
    editedVersionModel.setStepFreeAccess(version2.getStepFreeAccess());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
        Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when & then
    mvc.perform(put("/v1/relations/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
  }

  @Test
  void shouldNotUpdateRelationWithConstraintViolation() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    version1.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    version2.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersionModel editedVersionModel = new RelationVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setElementSloid(version2.getSloid());
    editedVersionModel.setReferencePointSloid(version2.getReferencePointSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setContrastingAreas(null);
    editedVersionModel.setTactileVisualMarks(null);
    editedVersionModel.setStepFreeAccess(null);
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
        Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when & then
    mvc.perform(put("/v1/relations/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details", hasSize(3)));
  }

  @Test
  void shouldNotUpdateRelationWhenIdNotFound() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    version1.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    version2.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersionModel editedVersionModel = new RelationVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setElementSloid(version2.getSloid());
    editedVersionModel.setReferencePointSloid(version2.getReferencePointSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setContrastingAreas(version2.getContrastingAreas());
    editedVersionModel.setTactileVisualMarks(version2.getTactileVisualMarks());
    editedVersionModel.setStepFreeAccess(version2.getStepFreeAccess());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
        Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when & then
    mvc.perform(put("/v1/relations/" + 12345678).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("Entity not found")));
  }

  @Test
  void shouldGetRelationVersionWithoutFilter() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version = RelationTestData.builderVersion1().build();
    version.setParentServicePointSloid(parentServicePointSloid);
    version.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version);
    //when & then
    mvc.perform(get("/v1/relations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id", is(version.getId().intValue())))
        .andExpect(jsonPath("$.objects[0].number.number", is(version.getNumber().getNumber())));
  }

  @Test
  void shouldGetRelationVersionsWithFilter() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version = RelationTestData.builderVersion1().build();
    version.setParentServicePointSloid(parentServicePointSloid);
    version.setSloid("ch:1:sloid:7000:11");
    version.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version);
    //when & then
    mvc.perform(get("/v1/relations" +
            "?servicePointNumbers=1234567" +
            "&referencePointsloids=ch:1:sloid:7000:1" +
            "&sloids=ch:1:sloid:7000:11" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo()+
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1).format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id" , is(version.getId().intValue())));
  }

  @Test
  void shouldGetRelationVersionsWithArrayInFilter() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version = RelationTestData.builderVersion1().build();
    version.setParentServicePointSloid(parentServicePointSloid);
    version.setSloid("ch:1:sloid:7000:11");
    version.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version);
    //when & then
    mvc.perform(get("/v1/relations" +
            "?servicePointNumbers=1234567&servicePointNumbers=1000000" +
            "&referencePointSloid=ch:1:sloid:7000:1&sloids=ch:1:sloid:54321" +
            "&sloids=ch:1:sloid:7000:11&ch:1:sloid:7000:111" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo()+
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1).format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id" , is(version.getId().intValue())));
  }

  @Test
  void shouldGetRelationVersionsFromReferencePointType() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    version1.setSloid("ch:1:sloid:7000:11");
    version1.setReferencePointSloid(referencePointSloid);
    version1.setReferencePointElementType(ReferencePointElementType.TOILET);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().sloid("ch:1:sloid:7000:2").build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    version2.setSloid("ch:1:sloid:7000:22");
    version2.setReferencePointSloid(referencePointSloid);
    version2.setReferencePointElementType(PARKING_LOT);
    relationRepository.saveAndFlush(version2);
    //when & then
    mvc.perform(get("/v1/relations?&referencePointElementTypes=TOILET"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id" , is(version1.getId().intValue())));
  }

  @Test
  void shouldNotGetRelationVersions() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:7000:1";
    RelationVersion version = RelationTestData.builderVersion1().build();
    version.setParentServicePointSloid(parentServicePointSloid);
    version.setSloid("ch:1:sloid:7000:11");
    version.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version);
    //when & then
    mvc.perform(get("/v1/relations?servicePointNumbers=1000000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));
  }

  @Test
  public void shouldCallImportRelationsService() throws Exception {
    RelationCsvModel relationCsvModel1 = RelationCsvModel.builder()
            .sloid("ch:1:sloid:7000:1")
            .rpSloid("ch:1:sloid:5000:1")
            .didokCode(123)
            .tactVisualMarks(1)
            .contrastingAreas(1)
            .stepFreeAccess(1)
            .status(1)
            .elType("platform")
            .dsSloid("ch:1:sloid:7000")
            .build();

    List<RelationCsvModel> csvModels = new ArrayList<>();
    csvModels.add(relationCsvModel1);

    RelationCsvModelContainer relationCsvModelContainer = RelationCsvModelContainer.builder()
            .csvModels(csvModels)
            .build();

    List<RelationCsvModelContainer> containers = new ArrayList<>();
    containers.add(relationCsvModelContainer);

    RelationImportRequestModel requestModel = new RelationImportRequestModel(containers);

    mvc.perform(post("/v1/relations/import")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(requestModel)))
            .andExpect(status().isOk());

    verify(relationImportService, times(1)).importRelations(any());
  }
}
