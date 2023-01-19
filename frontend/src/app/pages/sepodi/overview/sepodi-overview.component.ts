import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
  LngLatBoundsLike,
  Map,
  ResourceTypeEnum,
  SourceSpecification,
  StyleSpecification,
} from 'maplibre-gl';
import { AuthService } from '../../../core/auth/auth.service';

const MAP_STYLE: StyleSpecification = {
  version: 8,
  sources: {
    osm: {
      type: 'raster',
      tiles: [
        'https://wmts.geo.admin.ch/1.0.0/ch.swisstopo.pixelkarte-farbe/default/current/3857/{z}/{x}/{y}.jpeg',
      ],
      tileSize: 256,
      attribution: '&copy; OpenStreetMap Contributors',
      maxzoom: 19,
      bounds: [5.140242, 45.3981812, 11.47757, 48.230651],
    },
  },
  layers: [
    {
      id: 'osm',
      type: 'raster',
      source: 'osm',
      paint: {
        'raster-opacity': 0.85,
      },
    },
  ],
};

const SWISS_BOUNDING_BOX: LngLatBoundsLike = [
  // CH bounds;
  [5.7349, 45.6755],
  [10.6677, 47.9163],
];

const sourceName = 'geodata';
const layerNameThatMustMatch = 'servicepoints';

/* when source "id" not specified, layer and source share the same id */
const TILES_SOURCE: any = {
  type: 'vector',
  minzoom: 5,
  maxzoom: 20,
  tiles: [
    `http://localhost:8888/service-point-directory/v1/geodata/${layerNameThatMustMatch}/{z}/{x}/{y}.pbf`,
  ],
};

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent implements AfterViewInit, OnDestroy {
  map: Map | undefined;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private authService: AuthService) {}

  ngAfterViewInit() {
    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: MAP_STYLE,
      bounds: SWISS_BOUNDING_BOX,
      transformRequest: (url: string, resourceType?: ResourceTypeEnum) => {
        if (resourceType === 'Tile' && url.startsWith('http://localhost')) {
          return {
            url: url,
            headers: { Authorization: 'Bearer ' + this.authService.accessToken },
          };
        }
        return { url };
      },
    });

    this.map.once('style.load', () => {
      this.map?.addLayer({
        id: sourceName,
        'source-layer': layerNameThatMustMatch,
        source: TILES_SOURCE,
        type: 'circle',
      });
    });
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}
