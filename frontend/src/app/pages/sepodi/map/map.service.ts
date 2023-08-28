import { Injectable } from '@angular/core';
import {
  LngLat,
  LngLatLike,
  Map,
  MapGeoJSONFeature,
  MapMouseEvent,
  Popup,
  ResourceType,
} from 'maplibre-gl';
import { MAP_LAYER_NAME, MAP_SOURCE_NAME, MAP_STYLE_SPEC, MAP_ZOOM_DETAILS } from './map-style';
import { GeoJsonProperties, Point } from 'geojson';
import { MAP_STYLES, MapOptionsService, MapStyle } from './map-options.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { CoordinatePair } from '../../../api';
import { MapIconsService } from './map-icons.service';

export const mapZoomLocalStorageKey = 'map-zoom';
export const mapLocationLocalStorageKey = 'map-location';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  map!: Map;
  mapInitialized = new BehaviorSubject(false);
  selectedElement = new Subject<GeoJsonProperties>();
  currentMapStyle = MAP_STYLES[0];

  popup = new Popup({
    closeButton: true,
    closeOnClick: false,
    closeOnMove: false,
  });
  private _keepPopup = false;

  constructor(private mapOptionsService: MapOptionsService) {}

  initMap(mapContainer: HTMLElement) {
    this.map = new Map({
      container: mapContainer,
      style: MAP_STYLE_SPEC,
      bounds: this.mapOptionsService.getInitialBoundingBox(),
      transformRequest: (url: string, resourceType?: ResourceType) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });
    MapIconsService.addAllIconsToMap(this.map);
    this.initMapEvents();
    this.map.resize();

    return this.map;
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
    if (this.map) {
      this.map.removeFeatureState({ source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME });
    }
  }

  selectServicePoint(servicePointNumber: number) {
    this.deselectServicePoint();

    const renderedFeatures = this.map.queryRenderedFeatures({
      layers: [MAP_SOURCE_NAME],
      filter: ['==', 'number', servicePointNumber],
    });

    this.selectServicePointOnMap(renderedFeatures[0].properties.number);
  }

  removeMap() {
    this.map.remove();
  }

  private selectServicePointOnMap(servicePointNumber: string | number) {
    this.map.setFeatureState(
      { source: MAP_SOURCE_NAME, sourceLayer: MAP_LAYER_NAME, id: servicePointNumber },
      { selected: true }
    );
  }

  switchToStyle(style: MapStyle) {
    this.hideAllMapStyles();
    this.currentMapStyle = style;
    this.map.setLayoutProperty(style.id, 'visibility', 'visible');
    return this.currentMapStyle;
  }

  private hideAllMapStyles() {
    MAP_STYLES.forEach((style) => {
      this.map.setLayoutProperty(style.id, 'visibility', 'none');
    });
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
      this.map.on('mousemove', MAP_SOURCE_NAME, (e) => {
        if (this.showDetails()) {
          this.showPopup(e);
        }
      });
    });
    this.map.once('load', () => {
      this.mapInitialized.next(true);
    });
  }

  private showDetails(): boolean {
    return this.map.getZoom() >= MAP_ZOOM_DETAILS;
  }

  onClick(e: MapMouseEvent & { features?: GeoJSON.Feature[] }) {
    if (!this.showDetails() || !e.features) {
      return;
    }
    if (e.features.length == 1) {
      this.popup.remove();
      this.selectedElement.next(e.features[0].properties);
    } else {
      this.keepPopup = true;
    }
  }

  private initStoredMapBehaviour() {
    this.map.setZoom(Number(localStorage.getItem(mapZoomLocalStorageKey) ?? 7.2));
    this.map.on('zoomend', (e) => {
      localStorage.setItem(mapZoomLocalStorageKey, String(e.target.getZoom()));
    });

    const storedLocation = localStorage.getItem(mapLocationLocalStorageKey);
    if (storedLocation) {
      this.map.setCenter(JSON.parse(storedLocation) as LngLat);
    } else {
      this.map.setCenter({ lng: 8.088542571207768, lat: 46.79229542892091 });
    }
    this.map.on('moveend', (e) => {
      localStorage.setItem(mapLocationLocalStorageKey, JSON.stringify(e.target.getCenter()));
    });
  }

  showPopup(event: MapMouseEvent & { features?: MapGeoJSONFeature[] }) {
    if (!event.features || this.keepPopup) {
      return;
    }
    const coordinates = (event.features[0].geometry as Point).coordinates.slice() as LngLatLike;
    this.popup
      .setLngLat(coordinates)
      .setHTML(this.buildServicePointPopupInformation(event.features))
      .addTo(this.map);
    this.popup.on('close', () => {
      this.keepPopup = false;
    });
    this.popup.on('click', () => {
      this.keepPopup = true;
    });
  }

  buildServicePointPopupInformation(features: MapGeoJSONFeature[]) {
    let popupHtml = '';

    features.forEach((point) => {
      let formattedNumber = String(point.properties.number);
      formattedNumber = formattedNumber.slice(0, 2) + ' ' + formattedNumber.slice(2);
      popupHtml += `<a href="service-point-directory/service-points/${point.properties.number}" "><b>${formattedNumber}</b> - ${point.properties.designationOfficial}</a> <br/>`;
    });

    return popupHtml;
  }

  get keepPopup() {
    return this._keepPopup;
  }

  set keepPopup(value: boolean) {
    this._keepPopup = value;
    if (this._keepPopup) {
      this.setPopupToFixed();
    }
  }

  setPopupToFixed() {
    this.popup.getElement().classList.add('fixed-popup');
  }
}
