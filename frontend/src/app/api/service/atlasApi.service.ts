import { UserService } from '../../core/auth/user/user.service';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';

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

  protected paramsOf(params: {[key: string]: any}): HttpParams {
    let queryParameters = new HttpParams();

    Object.keys(params).forEach(key => {
      if (Array.isArray(params[key])){
        params[key].forEach((element) => {
          queryParameters = this.addToHttpParams(queryParameters, element, key);
        });
      } else {
        if (params[key] !== undefined && params[key] !== null) {
          queryParameters = this.addToHttpParams(queryParameters, params[key], key);
        }
      }
    })

    return queryParameters;
  }

  protected validateParams(params: { [key: string]: any }): void {
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === undefined) {
        throw new Error(`Required parameter '${key}' is null or undefined.`);
      }
    });
  }

  protected get<T>(path: string, params?: HttpParams): Observable<T> {
    return this.httpClient.get<T>(`${this.basePath}${path}`,
      {
        responseType: 'json',
        headers: new HttpHeaders({
          'Accept': '*/*',
        }),
        params
      },
    );
  }

  protected getBlob(path: string, params?: HttpParams): Observable<Blob> {
    return this.httpClient.get(`${this.basePath}${path}`,
      {
        responseType: 'blob',
        headers: new HttpHeaders({
          'Accept': '*/*',
        }),
        params
      },
    );
  }

  protected put<T>(path: string, body: any): Observable<T> {
    return this.httpClient.put<T>( `${this.basePath}${path}`, body,
      {
        responseType: 'json',
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
      },
    );
  }

  protected post<T>(path: string, body: any): Observable<T> {
    return this.httpClient.post<T>( `${this.basePath}${path}`, body,
      {
        responseType: 'json',
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
      },
    );
  }

  protected delete<T>(path: string): Observable<T> {
    return this.httpClient.delete<T>( `${this.basePath}${path}`);
  }

  // todo: simplify
  protected addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
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
