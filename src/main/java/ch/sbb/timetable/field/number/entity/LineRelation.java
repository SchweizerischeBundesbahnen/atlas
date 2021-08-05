package ch.sbb.timetable.field.number.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "timetable_field_line_relation")
public class LineRelation {

    private static final String TIMETABLE_FIELD_LINE_RELATION_SEQ = "timetable_field_line_relation_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TIMETABLE_FIELD_LINE_RELATION_SEQ)
    @SequenceGenerator(name = TIMETABLE_FIELD_LINE_RELATION_SEQ, sequenceName = TIMETABLE_FIELD_LINE_RELATION_SEQ, allocationSize = 1, initialValue = 1000)
    private Long id;

    private String slnid;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_field_version_id")
    private Version version;
}
