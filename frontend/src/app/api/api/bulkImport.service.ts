import {Inject, Injectable, Optional} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';

import {ApplicationType, BulkImportRequest, BulkImportResult, BusinessObjectType, ImportType} from '../model/models';
import {Configuration} from "../configuration";
import {BASE_PATH} from "../variables";


@Injectable({
  providedIn: 'root'
})
export class BulkImportService {

    protected basePath = 'http://localhost';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, configuration: Configuration) {
      if (configuration) {
        this.configuration = configuration;
      }
      if (typeof this.configuration.basePath !== 'string') {
        this.configuration.basePath = basePath;
      }
    }

    public downloadTemplate(
      applicationType: ApplicationType,
      objectType: BusinessObjectType,
      importType: ImportType,
    ): Observable<Blob> {
      if (!applicationType || !objectType || !importType) {
        throw new Error('Required parameters were null or undefined when calling downloadTemplate.');
      }

      const url = `${this.configuration.basePath}/bulk-import-service/v1/import/bulk/template/${encodeURIComponent(String(applicationType))}/${encodeURIComponent(String(objectType))}/${encodeURIComponent(String(importType))}`;

      let headers = this.defaultHeaders;
      headers = headers.set('Accept', '*/*');

      return this.httpClient.get(url, {
          responseType: "blob",
          withCredentials: this.configuration.withCredentials,
          headers: headers
      }).pipe(
          catchError(this.handleError)
        );
    }

    public getBulkImportResults(id: number): Observable<BulkImportResult> {
        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling getBulkImportResults.');
        }
        const url = `${this.configuration.basePath}/bulk-import-service/v1/import/bulk/${encodeURIComponent(String(id))}`;
        let headers = this.defaultHeaders;
        headers = headers.set('Accept', '*/*')

        return this.httpClient.get<BulkImportResult>(url, {
            withCredentials: this.configuration.withCredentials,
            headers: headers
        }).pipe(
          catchError(this.handleError)
        );
    }

  startBulkImport(bulkImportRequest: BulkImportRequest, file: Blob): Observable<any> {
    if (!bulkImportRequest || !file) {
      throw new Error('BulkImportRequest or File was null or undefined when calling startBulkImport.');
    }
    const url = `${this.configuration.basePath}/bulk-import-service/v1/import/bulk`;
    const formData: FormData = new FormData();

    formData.append('bulkImportRequest', new Blob([JSON.stringify(bulkImportRequest)], { type: 'application/json' }));
    formData.append('file', file);

    return this.httpClient.post(url, formData, {
      withCredentials: this.configuration.withCredentials
    }).pipe(
        catchError(this.handleError)
      );
  }

  private handleError() {
    let errorMessage = 'Unexpected Error occured!';
    return throwError(() => errorMessage);
  }
}
