import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {ApplicationType, BulkImportRequest, BulkImportResult, BusinessObjectType, ImportType} from '../../model/models';
import {AtlasApiService} from "../atlasApi.service";


@Injectable({
  providedIn: 'root'
})
export class BulkImportService {

  private readonly atlasApiService = inject(AtlasApiService);

  public downloadTemplate(
    applicationType: ApplicationType,
    objectType: BusinessObjectType,
    importType: ImportType,
  ): Observable<Blob> {
    this.atlasApiService.validateParams({applicationType, objectType, importType});

    const url = `/bulk-import-service/v1/import/bulk/template/${encodeURIComponent(applicationType)}/${encodeURIComponent(String(objectType))}/${encodeURIComponent(importType)}`;

    return this.atlasApiService.getBlob(url);
  }

  public getBulkImportResults(id: number): Observable<BulkImportResult> {
    this.atlasApiService.validateParams({id})

    const url = `/bulk-import-service/v1/import/bulk/${encodeURIComponent(id)}`;

    return this.atlasApiService.get(url);
  }


  public startBulkImport(bulkImportRequest: BulkImportRequest, file: Blob): Observable<any> {
    this.atlasApiService.validateParams({bulkImportRequest, file});

    const url = `/bulk-import-service/v1/import/bulk`;

    return this.atlasApiService.post(url, this.atlasApiService.createFormData({bulkImportRequest, file}));
  }
}
