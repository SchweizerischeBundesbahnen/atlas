import {UserService} from '../../core/auth/user/user.service';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';

type NotBlob<T> = T extends Blob ? unknown : T;

@Injectable({
  providedIn: "root"
})
export class AtlasApiService {

  private readonly httpClient = inject(HttpClient);
  private readonly userService = inject(UserService);

  private readonly acceptAllHeaders = new HttpHeaders({ 'Accept': '*/*' });
  private readonly createUpdateOptions: { responseType: 'json', headers: HttpHeaders } = {
    responseType: 'json',
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  paramsOf(params: { [key: string]: any }): HttpParams {
    let queryParameters = new HttpParams();

    Object.keys(params).forEach(key => {
      if (Array.isArray(params[key])) {
        params[key].forEach((element) => {
          queryParameters = this.addToHttpParams(queryParameters, element, key);
        });
      } else {
        if (params[key] !== undefined && params[key] !== null) {
          queryParameters = this.addToHttpParams(queryParameters, params[key], key);
        }
      }
    });

    return queryParameters;
  }

  validateParams(params: { [key: string]: any }): void {
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        throw new Error(`Required parameter '${key}' is null or undefined.`);
      }
    });
  }

  get<T>(path: string, params?: HttpParams): Observable<NotBlob<T>> {
    const url = `${this.basePath}${path}`;
    return this.httpClient.get<NotBlob<T>>(url, {
      headers: this.acceptAllHeaders,
      params,
      responseType: 'json'
    });
  }

  getBlob(path: string, params?: HttpParams): Observable<Blob> {
    const url = `${this.basePath}${path}`;
    return this.httpClient.get(url, {
      headers: this.acceptAllHeaders,
      params,
      responseType: 'blob'
    });
  }

  put<T>(path: string, body: any, options?: { responseType?: 'json', headers?: HttpHeaders }): Observable<T> {
    return this.httpClient.put<T>(`${this.basePath}${path}`, body, options ?? this.createUpdateOptions);
  }

  post<T>(path: string, body: any = null, options?: { responseType?: 'json', headers?: HttpHeaders }): Observable<T> {
    return this.httpClient.post<T>(`${this.basePath}${path}`, body, options ?? this.createUpdateOptions);
  }

  delete<T>(path: string): Observable<T> {
    return this.httpClient.delete<T>(`${this.basePath}${path}`);
  }

  createFormData(params: { [key: string]: any }): FormData {
    const formData: FormData = new FormData();

    Object.keys(params).forEach(key => {

      if(Array.isArray(params[key])){
        params[key].forEach((element) => {
          formData.append(key, element)
        })
      }

      else if(params[key] instanceof Blob) {
        formData.append(key, params[key]);
      }

      else {
        formData.append(key, this.createBlob(params[key]));
      }
    });

    return formData;
  }

  private createBlob(param: any): Blob {
    return new Blob([JSON.stringify(param)], { type: 'application/json' });
  }

  private get basePath() {
    if (this.userService.loggedIn) {
      return environment.atlasApiUrl;
    } else {
      return environment.atlasUnauthApiUrl;
    }
  }

  private addToHttpParams(httpParams: HttpParams, value: any, key: string): HttpParams {
    if (value instanceof Date) {
      return httpParams.append(key, (value as Date).toISOString().substr(0, 10));
    } else {
      return httpParams.append(key, value);
    }
  }
}
