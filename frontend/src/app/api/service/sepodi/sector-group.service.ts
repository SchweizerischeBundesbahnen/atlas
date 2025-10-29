import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CreateSectorGroupVersion } from '../../model/createSectorGroupVersion';
import { ReadSectorVersion } from '../../model/readSectorVersion';
import { ReadSectorGroupVersion } from '../../model/readSectorGroupVersion';
import { UpdateSectorGroupVersion } from '../../model/updateSectorGroupVersion';

@Injectable({
  providedIn: 'root'
})
export class SectorGroupService {
  private readonly BASE_PATH = '/service-point-directory/v1/sector-groups';
  private readonly BASE_PATH_INTERNAL = '/service-point-directory/internal/sector-groups';
  private readonly atlasApiService = inject(AtlasApiService);


  public getSectorGroup(sectorGroupSloid: string): Observable<Array<ReadSectorGroupVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(sectorGroupSloid)}`);
  }

  public getSectorsBySectorGroupSloid(sectorGroupSloid: string): Observable<Array<ReadSectorVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH_INTERNAL}/${encodeURIComponent(sectorGroupSloid)}/sectors`);
  }

  public createSectorGroup(sectorGroupVersion: CreateSectorGroupVersion): Observable<ReadSectorGroupVersion> {
    this.atlasApiService.validateParams({ sectorGroupVersion });
    return this.atlasApiService.post(this.BASE_PATH, sectorGroupVersion);
  }

  public updateSectorGroup(id: number, sectorGroupVersion: UpdateSectorGroupVersion): Observable<ReadSectorGroupVersion[]> {
    this.atlasApiService.validateParams({ id, sectorGroupVersion: sectorGroupVersion });
    return this.atlasApiService.put(
      `${this.BASE_PATH}/${encodeURIComponent(String(id))}`, sectorGroupVersion);
  }
}
