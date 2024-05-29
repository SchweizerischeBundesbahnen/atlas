import {Injectable, Optional} from '@angular/core';
import {Configuration} from "../../api";
import {environment} from "../../../environments/environment";

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
}
