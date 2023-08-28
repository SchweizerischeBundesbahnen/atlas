import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import maplibregl, { Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';

@Component({
  selector: 'atlas-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  availableMapStyles = MAP_STYLES;
  currentMapStyle!: MapStyle;
  showMapStyleSelection = false;

  map!: Map;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapService: MapService) {}

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.map.dragRotate.disable();
    this.map.touchZoomRotate.disableRotation();
    this.currentMapStyle = this.mapService.currentMapStyle;
  }

  ngOnDestroy() {
    this.mapService.removeMap();
  }

  openStyleSelection() {
    this.showMapStyleSelection = true;
    this.map.once('click', () => {
      this.showMapStyleSelection = false;
    });
  }

  switchToStyle(style: MapStyle) {
    this.currentMapStyle = this.mapService.switchToStyle(style);
    this.showMapStyleSelection = false;
  }

  zoomIn() {
    const currentZoom = this.map.getZoom();
    let newZoom = currentZoom + 0.75;
    this.map.zoomTo(newZoom, { duration: 500 });
  }

  zoomOut() {
    const currentZoom = this.map.getZoom();
    let newZoom = currentZoom - 0.75;
    this.map.zoomTo(newZoom, { duration: 500 });
  }

  goHome() {
    const swissLongLat = [8.2275, 46.8182];

    this.map.flyTo({
      center: swissLongLat as maplibregl.LngLatLike,
      zoom: 7.25,
      speed: 0.8,
    });
  }
}
