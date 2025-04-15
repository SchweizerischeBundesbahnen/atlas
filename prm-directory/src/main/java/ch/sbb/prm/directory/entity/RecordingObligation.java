package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "recording_obligation")
@AtlasVersionable
public class RecordingObligation extends BaseEntity {

  @Id
  private String sloid;

  private boolean recordingObligation;

}
