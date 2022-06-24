package ch.sbb.line.directory;

import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.CoverageType;
import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.repository.CoverageRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("line-subline-coverage-correction")
public class LineSublineCoverageCorrectionTest {

  @Autowired
  private CoverageRepositoryExtension coverageRepository;

  @Autowired
  private SublineRepositoryExtension sublineRepository;


  @Test
  void makeMainLineLongerToCoverSublines() {
    List<Coverage> incompleteSublineCoverages = coverageRepository.findCoveragesByModelTypeAndCoverageType(
        ModelType.SUBLINE, CoverageType.INCOMPLETE);

    for (Coverage c : incompleteSublineCoverages) {
      SublineVersion sublineBySlnid;
      try {
      sublineBySlnid = sublineRepository.findAllBySlnidOrderByValidFrom(c.getSlnid())
                                                       .stream()
                                                       .filter((sl) -> sl.getValidFrom()
                                                                         .equals(c.getValidFrom()))
                                                       .findFirst()
          .orElseThrow(() -> new RuntimeException("Not found sublineVersion with slnid: " + c.getSlnid()));

      } catch (RuntimeException e){
        System.out.println(e.getMessage());
        continue;
      }

      List<SublineVersion> sublinesByMainlineSlnid = sublineRepository.getSublineVersionByMainlineSlnid(
          sublineBySlnid.getMainlineSlnid());

      sublinesByMainlineSlnid.sort(Comparator.comparing(SublineVersion::getValidFrom));
      LocalDate earliest = sublinesByMainlineSlnid.get(0).getValidFrom();
      sublinesByMainlineSlnid.sort(Comparator.comparing(SublineVersion::getValidTo));
      LocalDate last = sublinesByMainlineSlnid.get(sublinesByMainlineSlnid.size() - 1).getValidTo();
      System.out.println(earliest.toString());
      System.out.println(last.toString());
      // TODO: import v5 data to localhost db, update mainline, maybe check that updated mainline are not updated again
    }
  }
}

@Repository
interface SublineRepositoryExtension extends SublineVersionRepository {

}

@Repository
interface CoverageRepositoryExtension extends CoverageRepository {

  List<Coverage> findCoveragesByModelTypeAndCoverageType(ModelType modelType,
      CoverageType coverageType);

}
