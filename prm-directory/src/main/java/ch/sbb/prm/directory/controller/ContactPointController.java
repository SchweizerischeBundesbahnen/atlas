package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.api.ContactPointApiV1;
import ch.sbb.prm.directory.controller.model.ContactPointObjectRequestParams;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import ch.sbb.prm.directory.mapper.ContactPointVersionMapper;
import ch.sbb.prm.directory.search.ContactPointSearchRestrictions;
import ch.sbb.prm.directory.service.ContactPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ContactPointController implements ContactPointApiV1 {

  private final ContactPointService contactPointService;

  @Override
  public Container<ReadContactPointVersionModel> getContactPoints(Pageable pageable,
                                                                    ContactPointObjectRequestParams contactPointObjectRequestParams) {
    ContactPointSearchRestrictions searchRestrictions = ContactPointSearchRestrictions.builder()
            .pageable(pageable)
            .contactPointObjectRequestParams(contactPointObjectRequestParams)
            .build();

    Page<ContactPointVersion> contactPointVersions = contactPointService.findAll(searchRestrictions);

    return Container.<ReadContactPointVersionModel>builder()
            .objects(contactPointVersions.stream().map(ContactPointVersionMapper::toModel).toList())
            .totalCount(contactPointVersions.getTotalElements())
            .build();
  }

  @Override
  public ReadContactPointVersionModel createContactPoint(ContactPointVersionModel model) {
    ContactPointVersion contactPointVersion = contactPointService.createContactPoint(
        ContactPointVersionMapper.toEntity(model));
    return ContactPointVersionMapper.toModel(contactPointVersion);
  }

  @Override
  public List<ReadContactPointVersionModel> updateContactPoint(Long id, ContactPointVersionModel model) {
    ContactPointVersion contactPointVersion =
        contactPointService.getContactPointVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    ContactPointVersion editedVersion = ContactPointVersionMapper.toEntity(model);
    contactPointService.updateContactPointVersion(contactPointVersion, editedVersion);

    return contactPointService.getAllVersions(contactPointVersion.getSloid()).stream()
        .map(ContactPointVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadContactPointVersionModel> getContactPointVersions(String sloid) {
    return contactPointService.getAllVersions(sloid).stream().map(ContactPointVersionMapper::toModel).toList();
  }

}
