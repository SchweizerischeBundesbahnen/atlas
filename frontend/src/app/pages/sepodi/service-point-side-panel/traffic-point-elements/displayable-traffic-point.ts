import { CoordinatePair, TrafficPointElementType } from '../../../../api';

export interface DisplayableTrafficPoint {
  type: TrafficPointElementType;
  sloid: string;
  designation: string;
  coordinates: CoordinatePair;
}
