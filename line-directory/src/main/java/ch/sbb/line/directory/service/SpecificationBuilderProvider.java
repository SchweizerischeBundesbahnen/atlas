package ch.sbb.line.directory.service;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.Subline_;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpecificationBuilderProvider {

  private final SpecificationBuilder<Line> lineSpecificationBuilder;
  private final SpecificationBuilder<Subline> sublineSpecificationBuilder;

  public SpecificationBuilderProvider() {
    lineSpecificationBuilder = SpecificationBuilder.<Line>builder()
                                                   .stringAttributes(
                                                                     List.of(Line_.swissLineNumber,
                                                                         Line_.number,
                                                                         Line_.description,
                                                                         Line_.businessOrganisation,
                                                                         Line_.slnid))
                                                   .validFromAttribute(
                                                                     Line_.validFrom)
                                                   .validToAttribute(Line_.validTo)
                                                   .singleStringAttribute(
                                                                     Line_.swissLineNumber)
                                                   .build();
    sublineSpecificationBuilder = SpecificationBuilder.<Subline>builder()
                                                      .stringAttributes(
                                                                        List.of(
                                                                            Subline_.swissSublineNumber,
                                                                            Subline_.description,
                                                                            Subline_.swissLineNumber,
                                                                            Subline_.businessOrganisation,
                                                                            Subline_.slnid,
                                                                            Subline_.number))
                                                      .validFromAttribute(
                                                                        Subline_.validFrom)
                                                      .validToAttribute(
                                                                        Subline_.validTo)
                                                      .build();
  }

  public SpecificationBuilder<Line> getLineSpecificationBuilderService() {
    return lineSpecificationBuilder;
  }

  public SpecificationBuilder<Subline> getSublineSpecificationBuilderService() {
    return sublineSpecificationBuilder;
  }

}
