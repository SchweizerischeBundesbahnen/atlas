package ch.sbb.atlas.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;

interface DummyRepository extends JpaRepository<DummyEntity, Long> {

}