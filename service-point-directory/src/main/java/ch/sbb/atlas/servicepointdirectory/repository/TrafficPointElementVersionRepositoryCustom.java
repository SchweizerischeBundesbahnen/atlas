package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficPointElementVersionRepositoryCustom {

    List<TrafficPointElementVersion> blaBloBlu2(@Param("sboids") List<String> sboids, @Param("shorNumbers") List<Integer> shorNumbers);

}
