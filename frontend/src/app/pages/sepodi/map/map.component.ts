import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
import { Map, MapMouseEvent, Popup, ResourceType } from 'maplibre-gl';
import { MapOptionsService, SWISS_TOPO_BOUNDING_BOX } from './map-options.service';
import { MAP_SOURCE_NAME, MAP_STYLE_SPEC, MAP_ZOOM_DETAILS } from './map-style';
import { GeoJsonProperties } from 'geojson';

export interface MouseInfo {
  lng: number;
  lat: number;
  zoom: number;
}

@Component({
  selector: 'atlas-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  @Output() elementClicked = new EventEmitter<GeoJsonProperties>();

  map!: Map;
  mouseInfo!: MouseInfo;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapOptionsService: MapOptionsService) {}

  ngAfterViewInit() {
    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: MAP_STYLE_SPEC,
      bounds: this.mapOptionsService.getInitialBoundingBox(),
      maxBounds: SWISS_TOPO_BOUNDING_BOX,
      minZoom: 7,
      transformRequest: (url: string, resourceType?: ResourceType) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });
    this.initMapEvents();
  }

  private initMapEvents() {
    this.map.once('style.load', () => {
      this.map.on('mousemove', (e) => this.onMouseOver(e));

      this.map.on('click', MAP_SOURCE_NAME, (e) => this.onClick(e));
      this.map.on('mouseenter', MAP_SOURCE_NAME, () => {
        if (this.showDetails()) {
          this.map.getCanvas().style.cursor = 'pointer';
        }
      });
      this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
        this.map.getCanvas().style.cursor = '';
      });
    });
  }

  private showDetails(): boolean {
    return this.map.getZoom() >= MAP_ZOOM_DETAILS;
  }

  private onMouseOver(event: MapMouseEvent) {
    this.mouseInfo = {
      lng: event.lngLat.lng,
      lat: event.lngLat.lat,
      zoom: this.map.getZoom(),
    };
  }

  private onClick(e: MapMouseEvent & { features?: GeoJSON.Feature[] }) {
    if (!this.showDetails() || !e.features) {
      return;
    }
    this.elementClicked.emit(e.features[0].properties);
  }

  ngOnDestroy() {
    this.map.remove();
  }
}
