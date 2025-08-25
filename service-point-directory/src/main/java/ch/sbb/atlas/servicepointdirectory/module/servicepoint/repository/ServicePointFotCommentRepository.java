package ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository;

import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointFotComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePointFotCommentRepository extends JpaRepository<ServicePointFotComment, Integer> {

}
