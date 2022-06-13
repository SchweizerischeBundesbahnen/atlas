package ch.sbb.line.directory.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.enumaration.LineType;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@Immutable
@FieldNameConstants
@Entity
@Subselect(Line.VIEW_DEFINITION)
public class Line {

  private String swissLineNumber;

  private String number;

  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private LineType lineType;

  private String businessOrganisation;

  @Id
  private String slnid;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  static final String VIEW_DEFINITION = "select *"
      + "from ("
      + "         select f.*, v.valid_from, v.valid_to"
      + "         from ("
      + "                  select swiss_line_number,"
      + "                         number,"
      + "                         description,"
      + "                         status,"
      + "                         line_type,"
      + "                         business_organisation,"
      + "                         slnid"
      + "                  from ("
      + "                           select distinct on (slnid) *"
      + "                           from ((select distinct on (slnid) 1 as rank, *"
      + "                                  from line_version"
      + "                                  where valid_from <= current_timestamp"
      + "                                    and current_timestamp <= valid_to)"
      + "                                 union all"
      + "                                 (select distinct on (slnid) 2 as rank, *"
      + "                                  from line_version"
      + "                                  where valid_from >= current_timestamp"
      + "                                  order by slnid, valid_from)"
      + "                                 union all"
      + "                                 (select distinct on (slnid) 3 as rank, *"
      + "                                  from line_version"
      + "                                  where valid_to <= current_timestamp"
      + "                                  order by slnid, valid_to desc)) as ranked order by slnid, rank"
      + "                       ) as chosen"
      + "              ) f"
      + "                  join ("
      + "             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to"
      + "             from line_version"
      + "             group by slnid"
      + "         ) v on f.slnid = v.slnid"
      + "     ) as lines";
}
