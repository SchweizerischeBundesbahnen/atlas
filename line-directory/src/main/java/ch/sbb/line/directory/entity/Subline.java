package ch.sbb.line.directory.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.enumaration.SublineType;
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
@Subselect(Subline.VIEW_DEFINITION)
public class Subline {

  private String swissSublineNumber;

  private String number;

  private String description;

  private String swissLineNumber;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private SublineType sublineType;

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
      + "                  select swiss_subline_number,"
      + "                         description,"
      + "                         swiss_line_number,"
      + "                         number,"
      + "                         status,"
      + "                         subline_type,"
      + "                         business_organisation,"
      + "                         slnid"
      + "                  from ("
      + "                           select distinct on (slnid) *"
      + "                           from ((select distinct on (s.slnid) 1 as rank,"
      + "                                                               s.swiss_subline_number,"
      + "                                                               s.description,"
      + "                                                               l.swiss_line_number,"
      + "                                                               s.number,"
      + "                                                               s.status,"
      + "                                                               s.subline_type,"
      + "                                                               s.business_organisation,"
      + "                                                               s.slnid,"
      + "                                                               s.valid_from,"
      + "                                                               s.valid_to"
      + "                                  from subline_version s"
      + "                                           join (" + Line.VIEW_DEFINITION + ") l on s.mainline_slnid = l.slnid"
      + "                                  where s.valid_from <= current_timestamp"
      + "                                    and current_timestamp <= s.valid_to)"
      + "                                 union all"
      + "                                 (select distinct on (s.slnid) 2 as rank,"
      + "                                                               s.swiss_subline_number,"
      + "                                                               s.description,"
      + "                                                               l.swiss_line_number,"
      + "                                                               s.number,"
      + "                                                               s.status,"
      + "                                                               s.subline_type,"
      + "                                                               s.business_organisation,"
      + "                                                               s.slnid,"
      + "                                                               s.valid_from,"
      + "                                                               s.valid_to"
      + "                                  from subline_version s"
      + "                                           join (" + Line.VIEW_DEFINITION + ") l on s.mainline_slnid = l.slnid"
      + "                                  where s.valid_from >= current_timestamp"
      + "                                  order by s.slnid, s.valid_from)"
      + "                                 union all"
      + "                                 (select distinct on (s.slnid) 3 as rank,"
      + "                                                               s.swiss_subline_number,"
      + "                                                               s.description,"
      + "                                                               l.swiss_line_number,"
      + "                                                               s.number,"
      + "                                                               s.status,"
      + "                                                               s.subline_type,"
      + "                                                               s.business_organisation,"
      + "                                                               s.slnid,"
      + "                                                               s.valid_from,"
      + "                                                               s.valid_to"
      + "                                  from subline_version s"
      + "                                           join (" + Line.VIEW_DEFINITION + ") l on s.mainline_slnid = l.slnid"
      + "                                  where s.valid_to <= current_timestamp"
      + "                                  order by s.slnid, s.valid_to desc)) as ranked order by slnid, rank"
      + "                       ) as chosen"
      + "              ) f"
      + "                  join ("
      + "             select slnid, min(valid_from) as valid_from, max(valid_to) as valid_to"
      + "             from subline_version"
      + "             group by slnid"
      + "         ) v on f.slnid = v.slnid"
      + "     ) as sublines";
}
