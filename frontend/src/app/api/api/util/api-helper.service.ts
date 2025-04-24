import {Injectable} from '@angular/core';
import {UserService} from "../../../core/auth/user/user.service";
import {HttpHeaders} from "@angular/common/http";
import {throwError} from "rxjs";
import {environment} from "../../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ApiHelperService {

  constructor(private userService: UserService) {}

  public readonly DEFAULT_HTTP_HEADERS: HttpHeaders = new HttpHeaders({
    'Accept': '*/*'
  });

  public handleError() {
    return throwError(() => 'Unexpected Error occured!');
  }

  public validateParams(params: { [key: string]: any }): void {
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        throw new Error(`Required parameter '${key}' is null or undefined.`);
      }
    });
  }

  public createBlob(param: any): Blob {
    return new Blob([JSON.stringify(param)], { type: 'application/json' });
  }

  public createFormData(params: { [key: string]: any }): FormData {
    let formData: FormData = new FormData();

    Object.keys(params).forEach(key => {
      if(key === 'file') {
        formData.append(key, params[key]);
      }
      else {
        formData.append(key, this.createBlob(params[key]));
      }
    });
    return formData;
  }

  public getBasePath(): string {
    console.log("this user logged in ", this.userService.loggedIn ? `${environment.atlasApiUrl}` : `${environment.atlasUnauthApiUrl}`)
    return this.userService.loggedIn ? `${environment.atlasApiUrl}` : `${environment.atlasUnauthApiUrl}`
  }
}
