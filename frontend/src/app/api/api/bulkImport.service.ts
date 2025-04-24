import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';

import {ApplicationType, BulkImportRequest, BulkImportResult, BusinessObjectType, ImportType} from '../model/models';
import {ApiHelperService} from "./util/api-helper.service";


@Injectable({
  providedIn: 'root'
})
export class BulkImportService {

  constructor(protected httpClient: HttpClient, private apiHelperService: ApiHelperService) {}

  public downloadTemplate(
    applicationType: ApplicationType,
    objectType: BusinessObjectType,
    importType: ImportType,
  ): Observable<Blob> {
    this.apiHelperService.validateParams({applicationType, objectType, importType});

    const url = `${this.apiHelperService.getBasePath()}/bulk-import-service/v1/import/bulk/template/${encodeURIComponent(applicationType)}/${encodeURIComponent(String(objectType))}/${encodeURIComponent(importType)}`;

    return this.httpClient.get(url, {
      responseType: "blob",
      headers: this.apiHelperService.DEFAULT_HTTP_HEADERS
    }).pipe(
      catchError(this.apiHelperService.handleError)
    );
  }

  public getBulkImportResults(id: number): Observable<BulkImportResult> {
    this.apiHelperService.validateParams({id})

    const url = `${this.apiHelperService.getBasePath()}/bulk-import-service/v1/import/bulk/${encodeURIComponent(id)}`;

    return this.httpClient.get<BulkImportResult>(url, {
      headers: this.apiHelperService.DEFAULT_HTTP_HEADERS
    }).pipe(
      catchError(this.apiHelperService.handleError)
    );
  }


  public startBulkImport(bulkImportRequest: BulkImportRequest, file: Blob): Observable<any> {
    this.apiHelperService.validateParams({bulkImportRequest, file});

    const url = `${this.apiHelperService.getBasePath()}/bulk-import-service/v1/import/bulk`;

    return this.httpClient.post(url, this.apiHelperService.createFormData({bulkImportRequest, file})).pipe(
      catchError(this.apiHelperService.handleError)
    );
  }
}
