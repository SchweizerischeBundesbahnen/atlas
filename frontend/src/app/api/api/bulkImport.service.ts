import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';

import {ApplicationType, BulkImportRequest, BulkImportResult, BusinessObjectType, ImportType} from '../model/models';
import {environment} from "../../../environments/environment";
import {ApiHelper} from "./util/api-helper";


@Injectable({
  providedIn: 'root'
})
export class BulkImportService {

  constructor(protected httpClient: HttpClient) {}

  public downloadTemplate(
    applicationType: ApplicationType,
    objectType: BusinessObjectType,
    importType: ImportType,
  ): Observable<Blob> {
    ApiHelper.validateParams({applicationType, objectType, importType});

    const url = `${environment.atlasApiUrl}/bulk-import-service/v1/import/bulk/template/${encodeURIComponent(applicationType)}/${encodeURIComponent(String(objectType))}/${encodeURIComponent(importType)}`;

    return this.httpClient.get(url, {
      responseType: "blob",
      headers: ApiHelper.DEFAULT_HTTP_HEADERS
    }).pipe(
      catchError(ApiHelper.handleError)
    );
  }

  public getBulkImportResults(id: number): Observable<BulkImportResult> {
    ApiHelper.validateParams({id})

    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getBulkImportResults.');
    }
    const url = `${environment.atlasApiUrl}/bulk-import-service/v1/import/bulk/${encodeURIComponent(id)}`;

    return this.httpClient.get<BulkImportResult>(url, {
      headers: ApiHelper.DEFAULT_HTTP_HEADERS
    }).pipe(
      catchError(ApiHelper.handleError)
    );
  }


  public startBulkImport(bulkImportRequest: BulkImportRequest, file: Blob): Observable<any> {
    ApiHelper.validateParams({bulkImportRequest, file});

    const url = `${environment.atlasApiUrl}/bulk-import-service/v1/import/bulk`;

    return this.httpClient.post(url, ApiHelper.createFormData({bulkImportRequest, file})).pipe(
      catchError(ApiHelper.handleError)
    );
  }
}
