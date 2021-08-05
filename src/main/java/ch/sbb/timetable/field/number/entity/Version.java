package ch.sbb.timetable.field.number.entity;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.enumaration.Type;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "timetable_field_number_version")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "timetable_field_number_version_seq")
    private Long id;

    @OneToMany(mappedBy = "version", fetch = FetchType.EAGER)
    private Set<LineRelation> lineRelations;

    private String fpfnid;

    private String name;

    private String number;

    private String swissTimetableFieldNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDate;

    private String creator;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime editionDate;

    private String editor;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDate validFrom;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDate validTo;

    private String businessOrganisation;

    private String comment;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String nameCompact;


}
