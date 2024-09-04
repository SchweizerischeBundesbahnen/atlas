import {ApplicationType, BusinessObjectType, ImportType} from "../../../api";

export const OPTIONS_APPLICATION_TYPE: string[] = Object.values([
  ApplicationType.Sepodi,
  ApplicationType.Prm,
]);

 export const OPTIONS_OBJECT_TYPE: string[] = Object.values([
  BusinessObjectType.ReferencePoint,
  BusinessObjectType.ServicePoint,
  BusinessObjectType.LoadingPoint,
  BusinessObjectType.TrafficPoint,

   BusinessObjectType.StopPoint,
   BusinessObjectType.ContactPoint,
   BusinessObjectType.ParkingLot,
   BusinessObjectType.Relation,
   BusinessObjectType.Platform,
   BusinessObjectType.Toilet
]);

export const OPTIONS_SCENARIO: string[] = Object.values([
  ImportType.Create,
  ImportType.Terminate,
  ImportType.Update
]);

export const ALLOWED_FILE_TYPES_BULK_IMPORT: string[] =["text/csv", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]

