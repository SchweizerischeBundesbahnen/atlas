package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointFotComment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointFotCommentMapper {

  public static ServicePointFotCommentModel toModel(ServicePointFotComment entity) {
    if (entity == null) {
      return null;
    }
    return ServicePointFotCommentModel.builder()
        .fotComment(entity.getFotComment())
        .creationDate(entity.getCreationDate())
        .creator(entity.getCreator())
        .editionDate(entity.getEditionDate())
        .editor(entity.getEditor())
        .etagVersion(entity.getVersion())
        .build();
  }

  public static ServicePointFotComment toEntity(ServicePointFotCommentModel model, ServicePointNumber number) {
    return ServicePointFotComment.builder()
        .servicePointNumber(number.getValue())
        .fotComment(model.getFotComment())
        .creationDate(model.getCreationDate())
        .creator(model.getCreator())
        .editionDate(model.getEditionDate())
        .editor(model.getEditor())
        .version(model.getEtagVersion())
        .build();
  }

}


