export type ServicePointType =
  | 'SERVICE_POINT'
  | 'OPERATING_POINT'
  | 'STOP_POINT'
  | 'FARE_STOP';

export const ServicePointType = {
  ServicePoint: 'SERVICE_POINT' as ServicePointType,
  OperatingPoint: 'OPERATING_POINT' as ServicePointType,
  StopPoint: 'STOP_POINT' as ServicePointType,
  FareStop: 'FARE_STOP' as ServicePointType,
};
