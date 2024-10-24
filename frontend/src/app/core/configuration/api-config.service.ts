import {Injectable, Optional} from '@angular/core';
import {Configuration} from "../../api";
import {environment} from "../../../environments/environment";

/**
 *  Switches from the api-auth-gateway url for unauthenticated users
 *  to the actual atlas api url for authenticated users
 */
@Injectable({
  providedIn: 'root',
})
export class ApiConfigService {

  constructor(@Optional() private configuration: Configuration) {
  }

  setToAuthenticatedUrl() {
    if (this.configuration) {
      this.configuration.basePath = environment.atlasApiUrl;
    }
  }

  setToUnauthenticatedUrl() {
    if (this.configuration) {
      this.configuration.basePath = environment.atlasUnauthApiUrl;
    }
  }

  get apiBasePath() {
    return this.configuration.basePath;
  }
}
