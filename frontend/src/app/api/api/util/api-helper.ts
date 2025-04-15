import {HttpHeaders} from "@angular/common/http";
import {throwError} from "rxjs";

export const DEFAULT_HTTP_HEADERS = new HttpHeaders({
  'Accept': '*/*'
});


export function handleError() {
  return throwError(() => 'Unexpected Error occured!');
}

export function validateParams(params: { [key: string]: any }): void {
  Object.keys(params).forEach(key => {
    if (params[key] === null || params[key] === undefined) {
      throw new Error(`Required parameter '${key}' is null or undefined.`);
    }
  });
}

export function createBlob(param: any) {
  return new Blob([JSON.stringify(param)], {type: 'application/json'})
}

export function createFormdata(params: { [key: string]: any }){
  let formData: FormData = new FormData();

  Object.keys(params).forEach(key => {
    if(params[key] === 'file') {
      formData.append(key, params[key]);
    }
    else {
      formData.append(key, createBlob(params[key]));
    }
  });
  return formData;
}
