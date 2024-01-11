package ch.sbb.atlas.location.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SloidRepository {

  private final JdbcTemplate jdbcTemplate;

  public Integer getNextSeqValue(String seqName) {
    return jdbcTemplate.queryForObject("select nextval(?);", Integer.class, seqName);
  }

  public void insertSloid(String sloid) {
    jdbcTemplate.update("insert into sloid_allocated (sloid) values (?);", sloid);
  }

}
