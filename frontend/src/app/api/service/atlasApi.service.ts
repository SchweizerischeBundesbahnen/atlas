import {UserService} from "../../core/auth/user/user.service";
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {inject} from "@angular/core";

export class AtlasApiService {

  protected httpClient = inject(HttpClient);
  protected userService = inject(UserService);

  get basePath() {
    if (this.userService.loggedIn) {
      return environment.atlasApiUrl;
    } else {
      return environment.atlasUnauthApiUrl;
    }
  }

}
