import {ApplicationType, BusinessObjectType, ImportType} from "../../../api";

export const OPTIONS_APPLICATION_TYPE: string[] = Object.values([
  ApplicationType.Sepodi,
  ApplicationType.Prm,
]);

 export const OPTIONS_OBJECT_TYPE_SEPODI: string[] = Object.values([
  BusinessObjectType.ReferencePoint,
  BusinessObjectType.ServicePoint,
  BusinessObjectType.LoadingPoint,
  BusinessObjectType.TrafficPoint,
]);

export const OPTIONS_OBJECT_TYPE_PRM: string[] = Object.values([
  BusinessObjectType.StopPoint,
  BusinessObjectType.ContactPoint,
  BusinessObjectType.ParkingLot,
  BusinessObjectType.Relation,
  BusinessObjectType.Platform,
  BusinessObjectType.Toilet
]);

export const OPTIONS_OBJECT_TYPE_TIMETABLE_HEARING: string[] = Object.values([]);
export const OPTIONS_OBJECT_TYPE_BODI: string[] = Object.values([]);
export const OPTIONS_OBJECT_TYPE_LIDI: string[] = Object.values([]);
export const OPTIONS_OBJECT_TYPE_TTFN: string[] = Object.values([]);

export const OPTIONS_SCENARIO: string[] = Object.values([
  ImportType.Create,
  ImportType.Terminate,
  ImportType.Update
]);

export const ALLOWED_FILE_TYPES_BULK_IMPORT: string[] =["text/csv", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"]

