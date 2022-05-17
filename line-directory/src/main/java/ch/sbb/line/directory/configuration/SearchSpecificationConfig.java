package ch.sbb.line.directory.configuration;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.Subline_;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumber_;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchSpecificationConfig {

  @Bean
  public SpecificationBuilder<TimetableFieldNumber> timetableFieldNumberSpecificationBuilder() {
    return SpecificationBuilder.<TimetableFieldNumber>builder()
                               .stringAttributes(
                                   List.of(TimetableFieldNumber_.swissTimetableFieldNumber,
                                       TimetableFieldNumber_.description,
                                       TimetableFieldNumber_.ttfnid, TimetableFieldNumber_.number,
                                       TimetableFieldNumber_.businessOrganisation))
                               .validFromAttribute(TimetableFieldNumber_.validFrom)
                               .validToAttribute(TimetableFieldNumber_.validTo)
                               .build();
  }

  @Bean
  public SpecificationBuilder<Line> lineSpecificationBuilder() {
    return SpecificationBuilder.<Line>builder()
                               .stringAttributes(
                                   List.of(Line_.swissLineNumber, Line_.number, Line_.description,
                                       Line_.businessOrganisation, Line_.slnid))
                               .validFromAttribute(Line_.validFrom)
                               .validToAttribute(Line_.validTo)
                               .singleStringAttribute(Line_.swissLineNumber)
                               .build();
  }

  @Bean
  public SpecificationBuilder<Subline> sublineSpecificationBuilder() {
    return SpecificationBuilder.<Subline>builder()
                               .stringAttributes(
                                   List.of(Subline_.swissSublineNumber, Subline_.description,
                                       Subline_.swissLineNumber, Subline_.businessOrganisation,
                                       Subline_.slnid, Subline_.number))
                               .validFromAttribute(Subline_.validFrom)
                               .validToAttribute(Subline_.validTo)
                               .build();
  }
}
