import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RecordingObligation } from '../../model/recordingObligation';
import { AtlasApiService } from '../atlasApi.service';

@Injectable({
  providedIn: 'root',
})
export class PersonWithReducedMobilityService {

  private readonly RECORDING_OBLIGATION = '/prm-directory/v1/stop-points/recording-obligation';

  private readonly atlasApiService = inject(AtlasApiService);

  public getRecordingObligation(sloid: string): Observable<RecordingObligation> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.RECORDING_OBLIGATION}/${encodeURIComponent(String(sloid))}`);
  }

  public updateRecordingObligation(sloid: string, recordingObligation: RecordingObligation): Observable<void> {
    this.atlasApiService.validateParams({ sloid, recordingObligation });
    return this.atlasApiService.put(`${this.RECORDING_OBLIGATION}/${encodeURIComponent(String(sloid))}`, recordingObligation);
  }
}
