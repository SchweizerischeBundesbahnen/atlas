package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointGeolocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingPointGeolocationRepository extends
    JpaRepository<LoadingPointGeolocation, Long> {

}
