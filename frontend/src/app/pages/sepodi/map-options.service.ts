import { Injectable } from '@angular/core';
import { LngLatBoundsLike, RequestParameters, ResourceTypeEnum } from 'maplibre-gl';
import { AuthService } from '../../core/auth/auth.service';
import { environment } from '../../../environments/environment';

const SWISS_BOUNDING_BOX: LngLatBoundsLike = [
  // CH bounds;
  [5.7349, 45.6755],
  [10.6677, 47.9163],
];

@Injectable({
  providedIn: 'root',
})
export class MapOptionsService {
  constructor(private authService: AuthService) {}

  authoriseRequest(url: string, resourceType?: ResourceTypeEnum): RequestParameters {
    if (resourceType === 'Tile' && url.startsWith(environment.atlasApiUrl)) {
      return {
        url: url,
        headers: { Authorization: 'Bearer ' + this.authService.accessToken },
      };
    }
    return { url };
  }

  getInitialBoundingBox(): LngLatBoundsLike {
    // STAM: later  we will keep the latest user bbox in localStorage.
    return SWISS_BOUNDING_BOX;
  }
}
