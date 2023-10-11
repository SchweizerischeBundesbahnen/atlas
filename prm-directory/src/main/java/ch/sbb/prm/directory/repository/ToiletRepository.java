package ch.sbb.prm.directory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.ToiletVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ToiletRepository extends JpaRepository<ToiletVersion, Long>,
    JpaSpecificationExecutor<ToiletVersion> {

  List<ToiletVersion> findByParentServicePointSloid(String parentServicePointSloid);

  List<ToiletVersion> findAllByNumberOrderByValidFrom(ServicePointNumber number);

  @Modifying(clearAutomatically = true)
  @Query("update toilet_version v set v.version = (v.version + 1) where v.number = :number")
  void incrementVersion(ServicePointNumber number);
}
