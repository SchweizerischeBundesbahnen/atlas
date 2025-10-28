package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true, example = "BUS")
public enum TtfnMeanOfTransport {

  TRAIN,
  BUS,
  TRAM,
  BOAT,
  CABLE_CAR,
  CHAIRLIFT,
  CABLE_RAILWAY,
  RACK_RAILWAY,
  METRO

}
