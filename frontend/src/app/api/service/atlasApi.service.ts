import { UserService } from '../../core/auth/user/user.service';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';

type NotBlob<T> = T extends Blob ? undefined : T;

export class AtlasApiService {

  protected httpClient = inject(HttpClient);
  protected userService = inject(UserService);

  private readonly acceptAllHeaders = new HttpHeaders({ 'Accept': '*/*' });
  private readonly createUpdateOptions: { responseType: 'json', headers: HttpHeaders } = {
    responseType: 'json',
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    }),
  };

  get basePath() {
    if (this.userService.loggedIn) {
      return environment.atlasApiUrl;
    } else {
      return environment.atlasUnauthApiUrl;
    }
  }

  protected paramsOf(params: { [key: string]: any }): HttpParams {
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

  protected validateParams(params: { [key: string]: any }): void {
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        throw new Error(`Required parameter '${key}' is null or undefined.`);
      }
    });
  }

  protected get<T>(path: string, responseType: 'json', params?: HttpParams): Observable<NotBlob<T>>;
  protected get(path: string, responseType: 'blob', params?: HttpParams): Observable<Blob>;
  protected get<T>(path: string, responseType: 'json' | 'blob', params?: HttpParams): Observable<NotBlob<T> | Blob> {
    const url = `${this.basePath}${path}`;
    const options = {
      headers: this.acceptAllHeaders,
      params,
    };
    return responseType === 'json' ? this.httpClient.get<NotBlob<T>>(url, {
      ...options,
      responseType,
    }) : this.httpClient.get(url, { ...options, responseType });
  }

  protected put<T>(path: string, body: any, options?: { responseType?: 'json', headers?: HttpHeaders }): Observable<T> {
    return this.httpClient.put<T>(`${this.basePath}${path}`, body, options ?? this.createUpdateOptions);
  }

  protected post<T>(path: string, body: any = null, options?: { responseType?: 'json', headers?: HttpHeaders }): Observable<T> {
    return this.httpClient.post<T>(`${this.basePath}${path}`, body, options ?? this.createUpdateOptions);
  }

  protected delete<T>(path: string): Observable<T> {
    return this.httpClient.delete<T>(`${this.basePath}${path}`);
  }

  // TODO: Simplify with https://flow.sbb.ch/browse/ATLAS-2868
  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === 'object' && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === 'object') {
      if (Array.isArray(value)) {
        (value as any[]).forEach(elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key,
            (value as Date).toISOString().substr(0, 10));
        } else {
          throw Error('key may not be null if value is Date');
        }
      } else {
        Object.keys(value).forEach(k => httpParams = this.addToHttpParamsRecursive(
          httpParams, value[k], key != null ? `${key}.${k}` : k));
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error('key may not be null if value is not object or array');
    }
    return httpParams;
  }

}
