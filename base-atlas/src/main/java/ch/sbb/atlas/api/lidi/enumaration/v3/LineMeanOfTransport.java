package ch.sbb.atlas.api.lidi.enumaration.v3;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum LineMeanOfTransport {
  TRAIN,
  RACK_RAILWAY,
  METRO,
  TRAM,
  BUS,
  CABLE_RAILWAY,
  AERIAL_TRAMWAY,
  GONDOLA_LIFT,
  CHAIRLIFT,
  ELEVATOR,
  BOAT,
  FERRY
}
