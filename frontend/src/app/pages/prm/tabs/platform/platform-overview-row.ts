export interface PlatformOverviewRow {
  designation?: string;
  sloid: string;
  designationOperational?: string;
  validFrom?: Date;
  validTo?: Date;
  completion: 'COMPLETE' | 'INCOMPLETE' | 'NOT_STARTED';
}
