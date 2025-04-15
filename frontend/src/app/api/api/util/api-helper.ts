import {HttpHeaders} from "@angular/common/http";
import {throwError} from "rxjs";

export class ApiHelper {

  public static readonly DEFAULT_HTTP_HEADERS: HttpHeaders = new HttpHeaders({
    'Accept': '*/*'
  });

  public static handleError() {
    return throwError(() => 'Unexpected Error occured!');
  }

  public static validateParams(params: { [key: string]: any }): void {
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        throw new Error(`Required parameter '${key}' is null or undefined.`);
      }
    });
  }

  public static createBlob(param: any): Blob {
    return new Blob([JSON.stringify(param)], { type: 'application/json' });
  }

  public static createFormData(params: { [key: string]: any }): FormData {
    let formData: FormData = new FormData();

    Object.keys(params).forEach(key => {
      if(key === 'file') {
        formData.append(key, params[key]);
      }
      else {
        formData.append(key, ApiHelper.createBlob(params[key]));
      }
    });
    return formData;
  }
}
