import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { LngLatBoundsLike, Map, StyleSpecification } from 'maplibre-gl';

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

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent implements AfterViewInit, OnDestroy {
  map: Map | undefined;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  ngAfterViewInit() {
    const initialState = { lng: 8.0, lat: 46.5, zoom: 7 };

    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: MAP_STYLE,
      bounds: SWISS_BOUNDING_BOX,
    });
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}
