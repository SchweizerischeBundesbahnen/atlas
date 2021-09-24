package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "subline_version")
public class SublineVersion {

  private static final String LINE_RELATION_SEQ = "subline_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = LINE_RELATION_SEQ)
  @SequenceGenerator(name = LINE_RELATION_SEQ, sequenceName = LINE_RELATION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "line_version_id")
  private Version version;

  @Enumerated(EnumType.STRING)
  private SublineType type;

  private String slnid;

  private String description;

  private String shortName;

  private String longName;

  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  private String businessOrganisation;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime creationDate;

  private String creator;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  private String editor;
}
