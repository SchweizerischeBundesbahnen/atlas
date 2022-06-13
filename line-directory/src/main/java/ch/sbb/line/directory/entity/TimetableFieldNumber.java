package ch.sbb.line.directory.entity;

import ch.sbb.atlas.model.Status;
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
@Subselect(TimetableFieldNumber.VIEW_DEFINITION)
public class TimetableFieldNumber {

  @Id
  private String ttfnid;

  private String swissTimetableFieldNumber;

  private String number;

  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String businessOrganisation;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  static final String VIEW_DEFINITION = "select *"
      + "from ("
      + "         select f.*, v.valid_from, v.valid_to"
      + "         from ("
      + "                  select swiss_timetable_field_number,"
      + "                         number,"
      + "                         description,"
      + "                         status,"
      + "                         ttfnid,"
      + "                         business_organisation,"
      + "                         valid_from as vf"
      + "                  from ("
      + "                           select distinct on (ttfnid) *"
      + "                           from ((select distinct on (ttfnid) 1 as rank, *"
      + "                                  from timetable_field_number_version"
      + "                                  where valid_from <= current_timestamp"
      + "                                    and current_timestamp <= valid_to)"
      + "                                 union all"
      + "                                 (select distinct on (ttfnid) 2 as rank, *"
      + "                                  from timetable_field_number_version"
      + "                                  where valid_from >= current_timestamp"
      + "                                  order by ttfnid, valid_from)"
      + "                                 union all"
      + "                                 (select distinct on (ttfnid) 3 as rank, *"
      + "                                  from timetable_field_number_version"
      + "                                  where valid_to <= current_timestamp"
      + "                                  order by ttfnid, valid_to desc)) as ranked order by ttfnid, rank"
      + "                       ) as chosen"
      + "              ) f"
      + "                  join ("
      + "             select ttfnid, min(valid_from) as valid_from, max(valid_to) as valid_to"
      + "             from timetable_field_number_version"
      + "             group by ttfnid"
      + "         ) v on f.ttfnid = v.ttfnid"
      + "     ) as timetable_field_numbers";
}
