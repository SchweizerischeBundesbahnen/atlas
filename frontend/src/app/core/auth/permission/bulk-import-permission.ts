import {
  ApplicationRole,
  ApplicationType,
  Permission,
  PermissionRestrictionType,
} from '../../../api';
import { PermissionRestriction } from '../../../api/model/permissionRestriction';

export const BULK_IMPORT_APPLICATIONS = [
  ApplicationType.Sepodi,
  ApplicationType.Prm,
  ApplicationType.Lidi,
  ApplicationType.Ttfn,
];

export class BulkImportPermission {
  static hasAnyBulkImportPermission(permissions: Permission[]): boolean {
    return permissions
      .filter((i) => BULK_IMPORT_APPLICATIONS.includes(i.application))
      .some(
        (bulkImportApplication) =>
          bulkImportApplication.role === ApplicationRole.Supervisor ||
          this.hasExplicitBulkImportPermission(
            bulkImportApplication.permissionRestrictions
          )
      );
  }

  private static hasExplicitBulkImportPermission(
    restrictions: Array<PermissionRestriction>
  ) {
    return restrictions.some(
      (i) =>
        i.type === PermissionRestrictionType.BulkImport &&
        i.valueAsString === 'true'
    );
  }
}
