import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import maplibregl, { Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';
import { MapIcon, MapIconsService } from './map-icons.service';
import { MAP_SOURCE_NAME } from './map-style';
import { Subscription } from 'rxjs';
import { CoordinateTransformationService } from '../geography/coordinate-transformation.service';

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

  private isEditModeSubsription!: Subscription;

  map!: Map;

  marker = new maplibregl.Marker({ color: '#FF0000' });

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(
    private mapService: MapService,
    private coordinateTransformationService: CoordinateTransformationService
  ) {}

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    MapIconsService.getIconsAsImages().then((icons) => (this.legend = icons));

    this.handleMapClick();
  }

  ngOnDestroy() {
    this.mapService.removeMap();
    this.isEditModeSubsription.unsubscribe();
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

  handleMapClick() {
    const marker = new maplibregl.Marker({ color: '#FF0000' });

    const onMapClicked = (e: any) => {
      const clickedCoordinates = e.lngLat;
      const transformedCoordinates = transformCoordinates(clickedCoordinates);
      placeMarkerAndFlyTo(clickedCoordinates);
      this.mapService.clickedGeographyCoordinates.next(transformedCoordinates);
    };

    const transformCoordinates = (coordinates: any) => {
      const coordinatePair = {
        north: coordinates.lat,
        east: coordinates.lng,
      };
      return this.coordinateTransformationService.transform(coordinatePair, 'WGS84', 'LV95');
    };

    const placeMarkerAndFlyTo = (coordinates: any) => {
      marker.setLngLat(coordinates).addTo(this.map);
      this.map.flyTo({
        center: coordinates as maplibregl.LngLatLike,
        speed: 0.8,
      });
    };

    const enterEditMode = () => {
      this.map.getCanvas().style.cursor = 'crosshair';
      this.map.on(
        'mouseleave',
        MAP_SOURCE_NAME,
        () => (this.map.getCanvas().style.cursor = 'crosshair')
      );
      this.map.on('click', onMapClicked);
    };

    const exitEditMode = () => {
      marker.remove();
      this.map.off('click', onMapClicked);
      this.map.getCanvas().style.cursor = '';
      this.map.on('mouseleave', MAP_SOURCE_NAME, () => (this.map.getCanvas().style.cursor = ''));
      this.mapService.clickedGeographyCoordinates.next({ north: 0, east: 0 });
    };

    this.isEditModeSubsription = this.mapService.isEditMode.subscribe((isEditMode) => {
      if (isEditMode) {
        enterEditMode();
      } else {
        exitEditMode();
      }
    });
  }
}
