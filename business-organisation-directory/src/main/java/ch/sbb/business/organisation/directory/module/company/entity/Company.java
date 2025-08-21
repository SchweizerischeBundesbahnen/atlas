package ch.sbb.business.organisation.directory.module.company.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity(name = "company")
public class Company {

  @Id
  private String uicCode;

  private String name;

  private String url;

  @Column(columnDefinition = "DATE")
  private LocalDate startValidity;

  @Column(columnDefinition = "DATE")
  private LocalDate endValidity;

  private String shortName;

  private String freeText;

  private String countryCodeIso;

}
