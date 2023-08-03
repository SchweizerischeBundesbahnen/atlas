import { Injectable } from '@angular/core';
import { LngLat, Map, MapMouseEvent, ResourceType } from 'maplibre-gl';
import { MAP_LAYER_NAME, MAP_SOURCE_NAME, MAP_STYLE_SPEC, MAP_ZOOM_DETAILS } from './map-style';
import { GeoJsonProperties } from 'geojson';
import { MapOptionsService } from './map-options.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { CoordinatePair } from '../../../api';

export const mapZoomLocalStorageKey = 'map-zoom';
export const mapLocationLocalStorageKey = 'map-location';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  map!: Map;
  mapInitialized = new BehaviorSubject(false);
  selectedElement = new Subject<GeoJsonProperties>();

  constructor(private mapOptionsService: MapOptionsService) {}

  initMap(mapContainer: HTMLElement) {
    this.map = new Map({
      container: mapContainer,
      style: MAP_STYLE_SPEC,
      bounds: this.mapOptionsService.getInitialBoundingBox(),
      transformRequest: (url: string, resourceType?: ResourceType) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });
    this.initMapEvents();
    this.map.resize();

    return this.map;
  }

  private initMapEvents() {
    this.map.once('style.load', () => {
      this.initStoredMapBehaviour();
      this.deselectServicePoint();

      this.map.on('click', 'selected-sepo', (e) => this.onClick(e));
      this.map.on('mouseenter', MAP_SOURCE_NAME, () => {
        if (this.showDetails()) {
          this.map.getCanvas().style.cursor = 'pointer';
        }
      });
      this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
        this.map.getCanvas().style.cursor = '';
      });
    });
    this.map.once('load', () => {
      this.mapInitialized.next(true);
    });
  }

  private initStoredMapBehaviour() {
    this.map.setZoom(Number(localStorage.getItem(mapZoomLocalStorageKey)));
    this.map.on('zoomend', (e) => {
      localStorage.setItem(mapZoomLocalStorageKey, String(e.target.getZoom()));
    });

    const storedLocation = localStorage.getItem(mapLocationLocalStorageKey);
    if (storedLocation) {
      this.map.setCenter(JSON.parse(storedLocation) as LngLat);
    }
    this.map.on('moveend', (e) => {
      localStorage.setItem(mapLocationLocalStorageKey, JSON.stringify(e.target.getCenter()));
    });
  }

  centerOn(wgs84Coordinates: CoordinatePair | undefined) {
    this.map.resize();
    return new Promise((resolve, reject) => {
      if (wgs84Coordinates) {
        this.map
          .flyTo({ center: { lng: wgs84Coordinates.east, lat: wgs84Coordinates.north }, speed: 5 })
          .once('moveend', () => {
            resolve(true);
          });
      } else {
        reject('No Coordinates to go to');
      }
    });
  }

  deselectServicePoint() {
    this.map.removeFeatureState({ source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME });
  }

  selectServicePoint(servicePointNumber: number) {
    this.deselectServicePoint();

    const renderedFeatures = this.map.queryRenderedFeatures({
      layers: [MAP_SOURCE_NAME],
      filter: ['==', 'number', servicePointNumber],
    });

    this.selectServicePointOnMap(renderedFeatures[0].properties.number);
  }

  private selectServicePointOnMap(servicePointNumber: string | number) {
    this.map.setFeatureState(
      { source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME, id: servicePointNumber },
      { selected: true }
    );
  }

  private showDetails(): boolean {
    return this.map.getZoom() >= MAP_ZOOM_DETAILS;
  }

  private onClick(e: MapMouseEvent & { features?: GeoJSON.Feature[] }) {
    if (!this.showDetails() || !e.features) {
      return;
    }
    this.selectedElement.next(e.features[0].properties);
  }
}
