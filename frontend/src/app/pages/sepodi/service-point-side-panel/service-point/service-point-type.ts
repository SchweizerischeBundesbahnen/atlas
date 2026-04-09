export const ServicePointType = {
  ServicePoint: 'SERVICE_POINT',
  OperatingPoint: 'OPERATING_POINT',
  StopPoint: 'STOP_POINT',
  FareStop: 'FARE_STOP',
} as const;

export type ServicePointType =
  (typeof ServicePointType)[keyof typeof ServicePointType];
