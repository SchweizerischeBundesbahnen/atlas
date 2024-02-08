package ch.sbb.atlas.location.repository;

import ch.sbb.atlas.api.location.SloidType;
import java.util.Set;

public interface BaseRepository {

  String getEntityName(SloidType sloidType);
  Set<String> getAlreadyDistributedSloid(SloidType sloidType);

}
