import { Injectable } from '@angular/core';
import { RequestParameters, ResourceTypeEnum } from 'maplibre-gl';
import { AuthService } from '../../core/auth/auth.service';

@Injectable({
  providedIn: 'root',
})
export class MapOptionsService {
  constructor(private authService: AuthService) {}

  authoriseRequest(url: string, resourceType?: ResourceTypeEnum): RequestParameters {
    if (resourceType === 'Tile' && url.includes(window.location.host)) {
      return {
        url: url,
        headers: { Authorization: 'Bearer ' + this.authService.accessToken },
      };
    }
    return { url };
  }
}
