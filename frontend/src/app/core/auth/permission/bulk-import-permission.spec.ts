import {
  BULK_IMPORT_APPLICATIONS,
  BulkImportPermission,
} from './bulk-import-permission';
import {
  ApplicationRole,
  ApplicationType,
  PermissionRestrictionType,
} from '../../../api';

describe('BulkImportPermission', () => {
  it('may access BulkImport for supervisor', () => {
    const result = BulkImportPermission.hasAnyBulkImportPermission([
      {
        application: ApplicationType.Sepodi,
        role: ApplicationRole.Supervisor,
        permissionRestrictions: [],
      },
    ]);
    expect(result).toBeTrue();
  });

  it('may not access BulkImport for reader', () => {
    const result = BulkImportPermission.hasAnyBulkImportPermission([
      {
        application: ApplicationType.Sepodi,
        role: ApplicationRole.Reader,
        permissionRestrictions: [],
      },
    ]);
    expect(result).toBeFalse();
  });

  it('may not access BulkImport for writer', () => {
    const result = BulkImportPermission.hasAnyBulkImportPermission([
      {
        application: ApplicationType.Sepodi,
        role: ApplicationRole.Writer,
        permissionRestrictions: [],
      },
    ]);
    expect(result).toBeFalse();
  });

  it('may access BulkImport for writer with explicit import permission', () => {
    const result = BulkImportPermission.hasAnyBulkImportPermission([
      {
        application: ApplicationType.Sepodi,
        role: ApplicationRole.Writer,
        permissionRestrictions: [
          { type: PermissionRestrictionType.BulkImport, valueAsString: 'true' },
        ],
      },
    ]);
    expect(result).toBeTrue();
  });

  it('should have bulk import applications ', () => {
    expect(BULK_IMPORT_APPLICATIONS.length).toBe(4);

    expect(BULK_IMPORT_APPLICATIONS).toContain(ApplicationType.Sepodi);
    expect(BULK_IMPORT_APPLICATIONS).toContain(ApplicationType.Prm);
    expect(BULK_IMPORT_APPLICATIONS).toContain(ApplicationType.Lidi);
    expect(BULK_IMPORT_APPLICATIONS).toContain(ApplicationType.Ttfn);
  });
});
