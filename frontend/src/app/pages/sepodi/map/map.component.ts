import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import maplibregl, { Map, MapLibreGL } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';
import { MapIcon, MapIconsService } from './map-icons.service';
import proj4, { WGS84 } from 'proj4';

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
  test: any;
  legend!: MapIcon[];

  map!: Map;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapService: MapService, private cdr: ChangeDetectorRef) {}

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    MapIconsService.getIconsAsImages().then((icons) => (this.legend = icons));

    //Funktioniert
    //TODO: Destroy Subscription
    this.mapService.isEditModus.subscribe((isEdit) => {
      if (isEdit) {
        const marker = new maplibregl.Marker({ color: '#FF0000' });
        marker.remove();

        this.map.getCanvas().style.cursor = 'crosshair';

        //BUG: Funktioniert solang maus auf karte bleibt. Wenn maus über blauen punkt fährt ist
        //     das Kreuz weg.
        this.map.on('click', (e: any) => {
          const clickedCoordinates = e.lngLat;
          const transformationResult = proj4('WGS84', 'LV95', [
            clickedCoordinates.lng,
            clickedCoordinates.lat,
          ]);

          marker.setLngLat(clickedCoordinates).addTo(this.map);

          this.mapService.clickedCoordinates.next(transformationResult);

          this.map.flyTo({
            center: clickedCoordinates as maplibregl.LngLatLike,
            speed: 0.8,
          });
        });
      }
    });
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
}
