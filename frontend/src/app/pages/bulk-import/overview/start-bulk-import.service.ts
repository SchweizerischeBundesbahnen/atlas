import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {BulkImportRequest} from "../../../api";
import {Observable} from "rxjs";
import {ApiConfigService} from "../../../core/configuration/api-config.service";
import {ajax} from "rxjs/internal/ajax/ajax";
import {map} from "rxjs/operators";
import {OAuthService} from "angular-oauth2-oidc";

@Injectable({
  providedIn: 'root'
})
export class StartBulkImportService {

  constructor(private apiConfigService: ApiConfigService,
              private oAuthService: OAuthService) {
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
    return ajax({
      method: 'POST',
      url: `${this.apiConfigService.apiBasePath}/import-service-point/v1/import/bulk`,
      body: formParams,
      responseType: 'json',
      headers: {'Authorization': `Bearer ${this.oAuthService.getAccessToken()}`}
    })
      .pipe(map(() => {
      }));
  }

}
