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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "timetable_field_line_relation_seq")
    private Long id;

    private String slnid;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_field_version_id")
    private Version version;
}
