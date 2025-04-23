import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RecordingObligation } from '../model/recordingObligation';
import { AtlasApiService } from './atlasApi.service';

@Injectable({
  providedIn: 'root',
})
export class PersonWithReducedMobilityService extends AtlasApiService {

  public getRecordingObligation(sloid: string): Observable<RecordingObligation> {
    this.validateParams({sloid});
    const path = `/prm-directory/v1/stop-points/recording-obligation/${encodeURIComponent(String(sloid))}`;
    return this.get(path);
  }

  public updateRecordingObligation(sloid: string, recordingObligation: RecordingObligation): Observable<void> {
    this.validateParams({sloid,recordingObligation});
    const path = `/prm-directory/v1/stop-points/recording-obligation/${encodeURIComponent(String(sloid))}`;
    return this.put(path, recordingObligation);
  }
}
