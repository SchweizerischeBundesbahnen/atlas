package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.AVAILABLE_AT_CERTAIN_TIMES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.AVAILABLE_IF_BOOKED;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.COORDINATE_SCALE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.FALSE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NONE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARTIAL;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SANITARY_FACILITY_TOILET;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SANITARY_FACILITY_TOILET_WHEELCHAIR_ACCESSIBLE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_BUS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_CABLEWAY;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_FUNICULAR;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_LIFT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_METRO;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_RAIL;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_TRAM;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE_WATER;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRUE;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NetexValueMapper {

  public static Optional<String> limitation(StandardAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES -> Optional.of(TRUE);
      case NO -> Optional.of(FALSE);
      case PARTIALLY -> Optional.of(PARTIAL);
      case TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> limitation(BooleanOptionalAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES -> Optional.of(TRUE);
      case NO -> Optional.of(FALSE);
      case TO_BE_COMPLETED -> Optional.empty();
    };
  }

  public static Optional<String> limitation(LevelAccessWheelchairAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES -> Optional.of(TRUE);
      case YES_WITH_STAFF_ASSISTANCE -> Optional.of(PARTIAL);
      case NO -> Optional.of(FALSE);
      case TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> limitation(StepFreeAccessAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES, YES_WITH_LIFT, YES_WITH_RAMP -> Optional.of(TRUE);
      case NO -> Optional.of(FALSE);
      case TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> limitation(TactileVisualAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES, WITH_REMOTE_CONTROL -> Optional.of(TRUE);
      case PARTIALLY -> Optional.of(PARTIAL);
      case NO -> Optional.of(FALSE);
      case TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> limitation(VehicleAccessAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case PLATFORM_ACCESS_WITHOUT_ASSISTANCE -> Optional.of(TRUE);
      case PLATFORM_ACCESS_WITH_ASSISTANCE, PLATFORM_ACCESS_WITH_ASSISTANCE_WHEN_NOTIFIED -> Optional.of(PARTIAL);
      case PLATFORM_NOT_WHEELCHAIR_ACCESSIBLE -> Optional.of(FALSE);
      case TO_BE_COMPLETED -> Optional.empty();
    };
  }

  public static Optional<String> ramp(BoardingDeviceAttributeType value) {
    if (value == BoardingDeviceAttributeType.RAMPS) {
      return Optional.of(TRUE);
    }
    if (value == BoardingDeviceAttributeType.NO) {
      return Optional.of(FALSE);
    }
    return Optional.empty();
  }

  public static Optional<String> hoist(BoardingDeviceAttributeType value) {
    if (value == BoardingDeviceAttributeType.LIFTS) {
      return Optional.of(TRUE);
    }
    if (value == BoardingDeviceAttributeType.NO) {
      return Optional.of(FALSE);
    }
    return Optional.empty();
  }

  public static Optional<String> booleanFlag(StandardAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES -> Optional.of(TRUE);
      case NO -> Optional.of(FALSE);
      case PARTIALLY, TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> assistanceAvailability(StandardAttributeType value) {
    if (value == null) {
      return Optional.empty();
    }
    return switch (value) {
      case YES -> Optional.of(AVAILABLE_IF_BOOKED);
      case PARTIALLY -> Optional.of(AVAILABLE_AT_CERTAIN_TIMES);
      case NO -> Optional.of(NONE);
      case TO_BE_COMPLETED, NOT_APPLICABLE -> Optional.empty();
    };
  }

  public static Optional<String> sanitaryFacilityList(StandardAttributeType wheelchairToilet) {
    return Optional.of(wheelchairToilet == StandardAttributeType.YES
        ? SANITARY_FACILITY_TOILET_WHEELCHAIR_ACCESSIBLE : SANITARY_FACILITY_TOILET);
  }

  public static Optional<String> ticketOffice(ContactPointType type) {
    return type == ContactPointType.TICKET_COUNTER ? Optional.of(TRUE) : Optional.empty();
  }

  public static Optional<String> triState(NetexTriState value) {
    return Optional.ofNullable(value).map(v -> v.name().toLowerCase(Locale.ROOT));
  }

  public static Optional<String> text(String value) {
    return Optional.ofNullable(value).map(String::trim).filter(v -> !v.isEmpty());
  }

  public static Optional<String> coordinate(Double value) {
    return Optional.ofNullable(value)
        .map(v -> BigDecimal.valueOf(v).setScale(COORDINATE_SCALE, RoundingMode.HALF_UP).toPlainString());
  }

  public static Optional<String> transportMode(String meanOfTransport) {
    return text(meanOfTransport)
        .map(v -> v.toUpperCase(Locale.ROOT))
        .flatMap(v -> Arrays.stream(MeanOfTransport.values()).filter(m -> m.name().equals(v)).findFirst())
        .flatMap(NetexValueMapper::transportMode);
  }

  private static Optional<String> transportMode(MeanOfTransport mode) {
    return switch (mode) {
      case TRAIN, RACK_RAILWAY -> Optional.of(TRANSPORT_MODE_RAIL);
      case BUS -> Optional.of(TRANSPORT_MODE_BUS);
      case TRAM -> Optional.of(TRANSPORT_MODE_TRAM);
      case BOAT -> Optional.of(TRANSPORT_MODE_WATER);
      case CABLE_CAR, CHAIRLIFT -> Optional.of(TRANSPORT_MODE_CABLEWAY);
      case CABLE_RAILWAY -> Optional.of(TRANSPORT_MODE_FUNICULAR);
      case METRO -> Optional.of(TRANSPORT_MODE_METRO);
      case ELEVATOR -> Optional.of(TRANSPORT_MODE_LIFT);
      case ON_DEMAND, UNKNOWN -> Optional.empty();
    };
  }
}
