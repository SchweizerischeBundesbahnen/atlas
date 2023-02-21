package ch.sbb.line.directory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "timetable_field_line_relation")
public class TimetableFieldLineRelation {

  private static final String LINE_RELATION_SEQ = "timetable_field_line_relation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = LINE_RELATION_SEQ)
  @SequenceGenerator(name = LINE_RELATION_SEQ, sequenceName = LINE_RELATION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  private String slnid;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "timetable_field_version_id")
  private TimetableFieldNumberVersion timetableFieldNumberVersion;
}
