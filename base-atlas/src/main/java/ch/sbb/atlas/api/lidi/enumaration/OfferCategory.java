package ch.sbb.atlas.api.lidi.enumaration;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum OfferCategory {

  EV(null),

  IC(MeanOfTransport.TRAIN),
  EC(MeanOfTransport.TRAIN),
  EN(MeanOfTransport.TRAIN),
  IR(MeanOfTransport.TRAIN),
  RE(MeanOfTransport.TRAIN),
  R(MeanOfTransport.TRAIN),
  S(MeanOfTransport.TRAIN),
  SN(MeanOfTransport.TRAIN),
  PE(MeanOfTransport.TRAIN),
  EXT(MeanOfTransport.TRAIN),
  ATZ(MeanOfTransport.TRAIN),
  ICE(MeanOfTransport.TRAIN),
  TGV(MeanOfTransport.TRAIN),
  RJ(MeanOfTransport.TRAIN),
  TE2(MeanOfTransport.TRAIN),
  TER(MeanOfTransport.TRAIN),
  RB(MeanOfTransport.TRAIN),
  IRE(MeanOfTransport.TRAIN),

  T(MeanOfTransport.TRAM),
  TN(MeanOfTransport.TRAM),

  M(MeanOfTransport.METRO),

  CAX(MeanOfTransport.BUS),
  CAR(MeanOfTransport.BUS),
  EXB(MeanOfTransport.BUS),
  B(MeanOfTransport.BUS),
  BN(MeanOfTransport.BUS),
  RUB(MeanOfTransport.BUS),
  BP(MeanOfTransport.BUS),

  FUN(MeanOfTransport.CABLE_RAILWAY),

  PB(MeanOfTransport.CABLE_CAR),
  GB(MeanOfTransport.CABLE_CAR),

  SL(MeanOfTransport.CHAIRLIFT),

  ASC(MeanOfTransport.ELEVATOR),
  BAT(MeanOfTransport.BOAT),
  FAE(MeanOfTransport.BOAT);

  private final MeanOfTransport meanOfTransport;

  public static List<OfferCategory> from(MeanOfTransport meanOfTransport) {
    if (meanOfTransport == MeanOfTransport.UNKNOWN) {
      throw new IllegalArgumentException("MeanOfTransport [" + meanOfTransport + "] not supported by OfferCategory!");
    }
    List<OfferCategory> categories = new java.util.ArrayList<>(Arrays.stream(OfferCategory.values())
        .filter(offerCategory -> offerCategory.getMeanOfTransport() == meanOfTransport).toList());
    categories.add(OfferCategory.EV);
    return categories;
  }

}
