package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointNumberRepository extends JpaRepository<ServicePointVersion, Long> {

  @Query(value = "select number from available_service_point_numbers where country=:country order by number limit 1",
      nativeQuery = true)
  Integer getNextAvailableServicePointNumber(String country);

  @Modifying(clearAutomatically = true)
  @Query(value = "delete from available_service_point_numbers where number=:number and country=:country",
      nativeQuery = true)
  Integer deleteAvailableNumber(Integer number, String country);

}
