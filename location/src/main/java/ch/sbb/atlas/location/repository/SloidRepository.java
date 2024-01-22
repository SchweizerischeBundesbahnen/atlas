package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.servicepoint.Country;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SloidRepository {

  @Qualifier("locationJdbcTemplate")
  private final JdbcTemplate locationJdbcTemplate;

  public Set<String> getAllocatedSloid(){
    return new HashSet<>(locationJdbcTemplate.queryForList("""
            select distinct (sloid)
            from allocated_sloid where sloid is not null
            """, String.class));
  }

  public Integer getNextSeqValue(String seqName) {
    return locationJdbcTemplate.queryForObject("select nextval(?);", Integer.class, seqName);
  }

  public void insertSloid(String sloid, SloidType sloidType) {
    locationJdbcTemplate.update("insert into allocated_sloid (sloid,sloidType) values (?,?);", sloid, sloidType.name());
  }

  public String getNextAvailableSloid(Country country) {
    return locationJdbcTemplate.queryForObject(
        "select sloid from available_service_point_sloid where country = ? and claimed = false order by sloid limit 1;",
        String.class, country.name());
  }

  //  public int deleteAvailableSloid(String sloid, Country country) {
  //    return jdbcTemplate.update("delete from available_service_point_sloid where sloid = ? and country = ?;", sloid,
  //        country.name());
  //  }

  public int setAvailableSloidToUsed(String sloid, Country country) {
    return locationJdbcTemplate.update("update available_service_point_sloid set claimed = true where sloid = ? and country = ?;",
        sloid,
        country.name());
  }

}
