import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {BulkImportRequest} from "../../../api";
import {Observable} from "rxjs";
import {ApiConfigService} from "../../../core/configuration/api-config.service";

@Injectable({
  providedIn: 'root'
})
export class StartBulkImportService {

  constructor(private httpClient: HttpClient,
              private apiConfigService: ApiConfigService) {
  }

  public startServicePointImportBatch(bulkImportRequest: BulkImportRequest, file: Blob): Observable<void> {
    const formParams = new FormData();
    if (bulkImportRequest !== undefined) {
      formParams.append('bulkImportRequest', new Blob([JSON.stringify(bulkImportRequest)], {type: 'application/json'}));
    }
    if (file !== undefined) {
      formParams.append('file', file);
    }
    console.log("Executing POST");
    return this.httpClient.post<void>(`${this.apiConfigService.apiBasePath}/import-service-point/v1/import/bulk`,
      formParams, {responseType: 'json'});
  }

}
