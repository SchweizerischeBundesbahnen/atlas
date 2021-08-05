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
@Builder
@Entity(name = "timetable_field_number_version")
public class Version {

    private  static final String TIMETABLE_FIELD_NUMBER_VERSION_SEQ = "timetable_field_number_version_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TIMETABLE_FIELD_NUMBER_VERSION_SEQ)
    @SequenceGenerator(name = TIMETABLE_FIELD_NUMBER_VERSION_SEQ, sequenceName = TIMETABLE_FIELD_NUMBER_VERSION_SEQ, allocationSize = 1, initialValue = 1000)
    private Long id;

    @OneToMany(mappedBy = "version", fetch = FetchType.EAGER)
    private Set<LineRelation> lineRelations;

    private String ttfnid;

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
