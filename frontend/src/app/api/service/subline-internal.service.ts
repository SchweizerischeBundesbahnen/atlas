import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SublineInternalService extends AtlasApiService {

  public revokeSubline(slnid: string): Observable<void> {
    this.validateParams({ slnid });
    return this.post(`/line-directory/internal/sublines/${encodeURIComponent(String(slnid))}/revoke`);
  }

  public deleteSublines(slnid: string): Observable<void> {
    this.validateParams({ slnid });
    return this.delete(`/line-directory/internal/sublines/${encodeURIComponent(String(slnid))}`);
  }

}
