import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SublineInternalService {

  private readonly INTERNAL_SUBLINES = '/line-directory/internal/sublines';

  private readonly atlasApiService = inject(AtlasApiService);

  public revokeSubline(slnid: string): Observable<void> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.post(
      `${this.INTERNAL_SUBLINES}/${encodeURIComponent(String(slnid))}/revoke`);
  }

  public deleteSublines(slnid: string): Observable<void> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.delete(
      `${this.INTERNAL_SUBLINES}/${encodeURIComponent(String(slnid))}`);
  }

}
