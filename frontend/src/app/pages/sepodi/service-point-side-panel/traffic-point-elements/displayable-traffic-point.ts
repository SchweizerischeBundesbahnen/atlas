import { CoordinatePair, TrafficPointElementType } from '../../../../api';

export interface DisplayableTrafficPoint {
  type: TrafficPointElementType;
  coordinates: CoordinatePair;
}
