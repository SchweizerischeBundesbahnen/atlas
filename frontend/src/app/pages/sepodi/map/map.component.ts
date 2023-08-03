import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { Map } from 'maplibre-gl';
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
    this.currentMapStyle = this.mapService.currentMapStyle;
  }

  ngOnDestroy() {
    this.map.remove();
  }

  toggleStyleSelection() {
    this.showMapStyleSelection = !this.showMapStyleSelection;
  }

  switchToStyle(style: MapStyle) {
    this.mapService.switchToStyle(style);
    this.toggleStyleSelection();
    this.currentMapStyle = this.mapService.currentMapStyle;
  }
}
