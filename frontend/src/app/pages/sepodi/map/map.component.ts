import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import maplibregl, { Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';
import { MapIcon, MapIconsService } from './map-icons.service';
import proj4 from 'proj4';
import { MAP_SOURCE_NAME } from './map-style';

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
  legend!: MapIcon[];

  map!: Map;

  marker = new maplibregl.Marker({ color: '#FF0000' });

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapService: MapService) {}

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    MapIconsService.getIconsAsImages().then((icons) => (this.legend = icons));

    const marker = new maplibregl.Marker({ color: '#FF0000' });

    const handleMapClick = (e: any) => {
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
    };

    this.mapService.isEditMode.subscribe((isEditMode) => {
      if (isEditMode) {
        marker.remove();
        this.map.getCanvas().style.cursor = 'crosshair';
        this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
          this.map.getCanvas().style.cursor = 'crosshair';
        });
        this.map.on('click', handleMapClick);
      } else {
        marker.remove();
        this.map.off('click', handleMapClick);
        this.map.getCanvas().style.cursor = '';
        this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
          this.map.getCanvas().style.cursor = '';
        });
        this.mapService.clickedCoordinates.next([]);
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
