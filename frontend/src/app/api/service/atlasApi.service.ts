import {Injectable} from '@angular/core';
import {UserService} from "../../core/auth/user/user.service";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class AtlasApiService {

  protected basePath!: string;

  constructor(protected userService: UserService,) {
    if (userService.loggedIn) {
      this.basePath = environment.atlasApiUrl;
    } else {
      this.basePath = environment.atlasUnauthApiUrl;
    }
  }

}
