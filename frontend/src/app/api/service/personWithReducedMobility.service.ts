import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {RecordingObligation} from "../model/recordingObligation";
import {AtlasApiService} from "./atlasApi.service";
import {UserService} from "../../core/auth/user/user.service";

@Injectable({
  providedIn: 'root'
})
export class PersonWithReducedMobilityService extends AtlasApiService{

  constructor(protected httpClient: HttpClient, protected userService: UserService) {
    super(userService);
  }

  public getRecordingObligation(sloid: string): Observable<RecordingObligation> {
    if (sloid === null || sloid === undefined) {
      throw new Error('Required parameter sloid was null or undefined when calling getRecordingObligation.');
    }

    const path = `/prm-directory/v1/stop-points/recording-obligation/${encodeURIComponent(String(sloid))}`;
    return this.httpClient.request<RecordingObligation>('get', `${this.basePath}${path}`,
      {
        responseType: "json",
        headers: new HttpHeaders({
          'Accept': '*/*'
        }),
      }
    );
  }

  public updateRecordingObligation(sloid: string, recordingObligation: RecordingObligation): Observable<any> {
    if (sloid === null || sloid === undefined) {
      throw new Error('Required parameter sloid was null or undefined when calling updateRecordingObligation.');
    }
    if (recordingObligation === null || recordingObligation === undefined) {
      throw new Error('Required parameter recordingObligation was null or undefined when calling updateRecordingObligation.');
    }

    const path = `/prm-directory/v1/stop-points/recording-obligation/${encodeURIComponent(String(sloid))}`;
    return this.httpClient.request<any>('put', `${this.basePath}${path}`,
      {
        body: recordingObligation,
        responseType: "json",
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        }),
      }
    );
  }
}
