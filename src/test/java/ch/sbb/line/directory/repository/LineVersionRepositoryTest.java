package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.IntegrationTest;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public class LineVersionRepositoryTest {


  private static final LineVersion LINE_VERSION = LineTestData.lineVersion();

  private final LineVersionRepository lineVersionRepository;

  @Autowired
  public LineVersionRepositoryTest(LineVersionRepository lineVersionRepository) {
    this.lineVersionRepository = lineVersionRepository;
  }


  @Test
  void shouldGetSimpleVersion() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    LineVersion result = lineVersionRepository.findAll().get(0);

    //then
    assertThat(result).usingRecursiveComparison()
                      .ignoringActualNullFields()
                      .isEqualTo(LINE_VERSION);
    assertThat(result.getSlnid()).startsWith("ch:1:slnid:");
    assertThat(result.getCreationDate()).isNotNull();
    assertThat(result.getEditionDate()).isNotNull();
  }

  @Test
  void shouldUpdateSimpleLineVersion() {
    //given
    LineVersion result = lineVersionRepository.save(LINE_VERSION);


    //when
    result.setNumber("other number");
    result = lineVersionRepository.save(result);

    //then
    assertThat(result.getNumber()).isEqualTo("other number");
  }

  @Test
  void shouldGetCountVersions() {
    //given
    lineVersionRepository.save(LINE_VERSION);

    //when
    long result = lineVersionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    LineVersion lineVersion = lineVersionRepository.save(LINE_VERSION);
    lineVersionRepository.delete(lineVersion);

    //when
    List<LineVersion> result = lineVersionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }


}