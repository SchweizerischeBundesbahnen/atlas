export const TableFilterSearchType = {
  BUSINESS_ORGANISATION: 'BUSINESS_ORGANISATION',
  TIMETABLE_FIELD_NUMBER: 'TIMETABLE_FIELD_NUMBER',
  TRANSPORT_COMPANY: 'TRANSPORT_COMPANY',
};

export type TableFilterSearchType =
  (typeof TableFilterSearchType)[keyof typeof TableFilterSearchType];
