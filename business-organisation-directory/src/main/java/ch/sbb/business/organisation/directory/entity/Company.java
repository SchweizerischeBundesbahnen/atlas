package ch.sbb.business.organisation.directory.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity(name = "company")
public class Company {

  @Id
  private Long uicCode;

  private String name;

  private String nameAscii;

  private String url;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime startValidity;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime endValidity;

  private String shortName;

  private String freeText;

  private String countryCodeIso;

  private boolean passengerFlag;
  private boolean freightFlag;
  private boolean infrastructureFlag;
  private boolean otherCompanyFlag;
  private boolean neEntityFlag;
  private boolean ceEntityFlag;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime addDate;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime modifiedDate;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

}
