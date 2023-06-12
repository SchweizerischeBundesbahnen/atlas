package ch.sbb.atlas.transport.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "shared_transport_company")
public class SharedTransportCompany {

  @Id
  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  private String businessRegisterNumber;

}
