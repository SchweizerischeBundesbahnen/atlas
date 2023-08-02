import { Injectable } from '@angular/core';
import { Map, MapMouseEvent, ResourceType } from 'maplibre-gl';
import { MAP_LAYER_NAME, MAP_SOURCE_NAME, MAP_STYLE_SPEC, MAP_ZOOM_DETAILS } from './map-style';
import { GeoJsonProperties } from 'geojson';
import { MapOptionsService } from './map-options.service';
import { Subject } from 'rxjs';

export const mapZoomLocalStorageKey = 'map-zoom';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  map!: Map;

  selectedElement = new Subject<GeoJsonProperties>();

  constructor(private mapOptionsService: MapOptionsService) {}

  initMap(mapContainer: HTMLElement) {
    this.map = new Map({
      container: mapContainer,
      style: MAP_STYLE_SPEC,
      bounds: this.mapOptionsService.getInitialBoundingBox(),
      minZoom: 7,
      transformRequest: (url: string, resourceType?: ResourceType) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });
    this.initMapEvents();
    this.map.resize();

    return this.map;
  }

  private initMapEvents() {
    this.map.once('style.load', () => {
      this.map.zoomTo(Number(localStorage.getItem(mapZoomLocalStorageKey)));

      this.map.on('click', 'selected-sepo', (e) => this.onClick(e));
      this.map.on('mouseenter', MAP_SOURCE_NAME, () => {
        if (this.showDetails()) {
          this.map.getCanvas().style.cursor = 'pointer';
        }
      });
      this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
        this.map.getCanvas().style.cursor = '';
      });

      this.map.on('zoomend', (e) => {
        localStorage.setItem(mapZoomLocalStorageKey, String(e.target.getZoom()));
      });
    });
  }

  private showDetails(): boolean {
    return this.map.getZoom() >= MAP_ZOOM_DETAILS;
  }

  private onClick(e: MapMouseEvent & { features?: GeoJSON.Feature[] }) {
    if (!this.showDetails() || !e.features) {
      return;
    }

    this.map.removeFeatureState({ source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME });

    const clickedId = e.features[0].id;
    this.map.setFeatureState(
      { source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME, id: clickedId },
      { selected: true }
    );
    this.selectedElement.next(e.features[0].properties);
  }
}
