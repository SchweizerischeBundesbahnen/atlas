import { Injectable } from '@angular/core';
import { LngLatBoundsLike, RequestParameters, ResourceType } from 'maplibre-gl';
import { AuthService } from '../../../core/auth/auth.service';
import { environment } from '../../../../environments/environment';

const SWISS_BOUNDING_BOX: LngLatBoundsLike = [
  [5.7349, 45.6755],
  [10.6677, 47.9163],
];

export const SWISS_TOPO_BOUNDING_BOX: LngLatBoundsLike = [
  [5.140242, 45.3981812],
  [11.47757, 48.230651],
];

@Injectable({
  providedIn: 'root',
})
export class MapOptionsService {
  constructor(private authService: AuthService) {}

  authoriseRequest(url: string, resourceType?: ResourceType): RequestParameters {
    if (resourceType === ResourceType.Tile && url.startsWith(environment.atlasApiUrl)) {
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
