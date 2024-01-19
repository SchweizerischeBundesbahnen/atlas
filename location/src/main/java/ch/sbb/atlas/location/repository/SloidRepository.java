package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.servicepoint.Country;
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

  public String getNextAvailableSloid(Country country) {
    return jdbcTemplate.queryForObject(
        "select sloid from available_service_point_sloid where country = ? and used = false order by sloid limit 1;",
        String.class, country.name());
  }

  //  public int deleteAvailableSloid(String sloid, Country country) {
  //    return jdbcTemplate.update("delete from available_service_point_sloid where sloid = ? and country = ?;", sloid,
  //        country.name());
  //  }

  public int setAvailableSloidToUsed(String sloid, Country country) {
    return jdbcTemplate.update("update available_service_point_sloid set used = true where sloid = ? and country = ?;", sloid,
        country.name());
  }

}
