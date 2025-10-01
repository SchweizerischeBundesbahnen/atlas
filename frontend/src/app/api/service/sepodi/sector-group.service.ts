import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ReadSectorVersion } from '../../model/readSectorVersion';
import { CreateSectorVersion } from '../../model/createSectorVersion';

@Injectable({
  providedIn: 'root'
})
export class SectorGroupService {
  private readonly BASE_PATH = '/service-point-directory/v1/sector-groups';
  private readonly atlasApiService = inject(AtlasApiService);


  public getSectorGroup(sectorGroupSloid: string): Observable<Array<ReadSectorVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(sectorGroupSloid)}`);
  }

  //TODO
  public getSectorsBySectorGroupSloid(sectorGroupSloid: string) {
    return;
  }

  public createSectorGroup(sectorVersion: CreateSectorVersion): Observable<ReadSectorVersion> {
    this.atlasApiService.validateParams({ sectorVersion });
    return this.atlasApiService.post(this.BASE_PATH, sectorVersion);
  }

  public updateSectorGroup(id: number, sectorVersion: CreateSectorVersion): Observable<ReadSectorVersion[]> {
    this.atlasApiService.validateParams({ id, sublineVersionV2: sectorVersion });
    return this.atlasApiService.put(
      `${this.BASE_PATH}/${encodeURIComponent(String(id))}`, sectorVersion);
  }
}
