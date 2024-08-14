import {ApplicationRole, ApplicationType, Permission, PermissionRestrictionObject, PermissionRestrictionType} from "../../api";

export const BULK_IMPORT_APPLICATIONS = [ApplicationType.Sepodi, ApplicationType.Prm];

export class BulkImportUtil {

  static hasAnyBulkImportPermission(permissions: Permission[]): boolean {
    return permissions.filter(i => BULK_IMPORT_APPLICATIONS.includes(i.application))
      .some(bulkImportApplication => bulkImportApplication.role === ApplicationRole.Supervisor ||
        this.hasExplicitBulkImportPermission(bulkImportApplication.permissionRestrictions));
  }

  private static hasExplicitBulkImportPermission(restrictions: Array<PermissionRestrictionObject>) {
    return restrictions.some(i => i.type === PermissionRestrictionType.BulkImport && i.valueAsString === "true")
  }
}
