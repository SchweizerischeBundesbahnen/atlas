import { describe, expect, it } from 'vitest';
import { BulkImportFormGroupBuilder } from './bulk-import-form-group';
import { ApplicationType, BusinessObjectType, ImportType } from '../../../api';

describe('BulkImportFormGroupBuilder', () => {
  function buildFormGroup(objectType: BusinessObjectType, importType: ImportType) {
    const formGroup = BulkImportFormGroupBuilder.initFormGroup();
    formGroup.controls.applicationType.setValue(ApplicationType.Prm);
    formGroup.controls.objectType.setValue(objectType);
    formGroup.controls.importType.setValue(importType);
    return formGroup;
  }

  it('should map reduced platform terminate to PLATFORM', () => {
    const result = BulkImportFormGroupBuilder.buildBulkImport(
      buildFormGroup(BusinessObjectType.PlatformReduced, ImportType.Terminate)
    );

    expect(result.objectType).toBe(BusinessObjectType.Platform);
  });

  it('should map complete platform terminate to PLATFORM', () => {
    const result = BulkImportFormGroupBuilder.buildBulkImport(
      buildFormGroup(BusinessObjectType.PlatformComplete, ImportType.Terminate)
    );

    expect(result.objectType).toBe(BusinessObjectType.Platform);
  });

  it('should keep the object type for non terminate imports', () => {
    const result = BulkImportFormGroupBuilder.buildBulkImport(
      buildFormGroup(BusinessObjectType.PlatformReduced, ImportType.Update)
    );

    expect(result.objectType).toBe(BusinessObjectType.PlatformReduced);
  });
});
