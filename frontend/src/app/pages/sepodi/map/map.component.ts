import { AfterViewInit, Component, ElementRef, Input, OnDestroy, ViewChild } from '@angular/core';
import maplibregl, { Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';
import { MapIcon, MapIconsService } from './map-icons.service';

@Component({
  selector: 'atlas-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  availableMapStyles = MAP_STYLES;
  currentMapStyle!: MapStyle;
  showMapStyleSelection = false;
  showMapLegend = false;
  @Input() showSearch!: boolean;

  legend!: MapIcon[];

  map!: Map;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapService: MapService) {}

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    MapIconsService.getIconsAsImages().then((icons) => (this.legend = icons));
  }

  ngOnDestroy() {
    this.mapService.removeMap();
  }

  toggleStyleSelection() {
    this.showMapStyleSelection = !this.showMapStyleSelection;
    if (this.showMapStyleSelection) {
      this.showMapLegend = false;
    }

    this.map.once('click', () => {
      this.showMapStyleSelection = false;
    });
  }

  toggleLegend() {
    this.showMapLegend = !this.showMapLegend;
    if (this.showMapLegend) {
      this.showMapStyleSelection = false;
    }

    this.map.once('click', () => {
      this.showMapLegend = false;
    });
  }

  switchToStyle(style: MapStyle) {
    this.currentMapStyle = this.mapService.switchToStyle(style);
    this.showMapStyleSelection = false;
  }

  zoomIn() {
    const currentZoom = this.map.getZoom();
    const newZoom = currentZoom + 0.75;
    this.map.zoomTo(newZoom, { duration: 500 });
  }

  zoomOut() {
    const currentZoom = this.map.getZoom();
    const newZoom = currentZoom - 0.75;
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
