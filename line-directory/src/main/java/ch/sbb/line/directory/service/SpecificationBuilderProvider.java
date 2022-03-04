package ch.sbb.line.directory.service;

import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.Subline_;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SpecificationBuilderProvider {

  private final SpecificationBuilderService<Line> lineSpecificationBuilderService;
  private final SpecificationBuilderService<Subline> sublineSpecificationBuilderService;

  public SpecificationBuilderProvider() {
    lineSpecificationBuilderService = new SpecificationBuilderService<>(
        List.of(Line_.swissLineNumber, Line_.number, Line_.description, Line_.businessOrganisation, Line_.slnid),
        Line_.validFrom,
        Line_.validTo,
        Line_.swissLineNumber
    );
    sublineSpecificationBuilderService = new SpecificationBuilderService<>(
        List.of(Subline_.swissSublineNumber, Subline_.description, Subline_.swissLineNumber, Subline_.businessOrganisation, Subline_.slnid),
        Subline_.validFrom,
        Subline_.validTo,
        null
    );
  }

  public SpecificationBuilderService<Line> getLineSpecificationBuilderService() {
    return lineSpecificationBuilderService;
  }

  public SpecificationBuilderService<Subline> getSublineSpecificationBuilderService() {
    return sublineSpecificationBuilderService;
  }

}
