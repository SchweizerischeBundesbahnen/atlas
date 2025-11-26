package ch.sbb.atlas.api.workflow.tth.dossier;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum DossierStatus {
  ADDED,
  CANCELED,
  DOSSIER_BO_CHECK,
  DOSSIER_CANTON_CHECK,
  ACCEPTED,
  REJECTED,
  MOVED,
  DISSOLVED,

}
