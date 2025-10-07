import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ReadSectorVersion } from '../../model/readSectorVersion';
import { CreateSectorVersion } from '../../model/createSectorVersion';

@Injectable({
  providedIn: 'root',
})
export class SectorService {

  private readonly BASE_PATH = '/service-point-directory/v1/sectors';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSector(sectorSloid: string): Observable<Array<ReadSectorVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(sectorSloid)}`);
  }

  public createSector(sectorVersion: CreateSectorVersion): Observable<ReadSectorVersion> {
    this.atlasApiService.validateParams({ sectorVersion });
    return this.atlasApiService.post(this.BASE_PATH, sectorVersion);
  }

  public updateSector(id: number, sectorVersion: CreateSectorVersion): Observable<ReadSectorVersion[]> {
    this.atlasApiService.validateParams({ id, sectorVersion: sectorVersion });
    return this.atlasApiService.put(
      `${this.BASE_PATH}/${encodeURIComponent(String(id))}`, sectorVersion);
  }

}
