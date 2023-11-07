import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options.service';
import { Router } from '@angular/router';
import { Pages } from '../../pages';
import { take } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ApplicationType } from '../../../api';
import { filter } from 'rxjs/operators';
import { ServicePointIconType } from './service-point-icon-type';

export interface MapIcon {
  id: string;
  icon: HTMLImageElement;
}

@Component({
  selector: 'atlas-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss'],
})
export class MapComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() public isSidePanelOpen = false;

  public canCreateServicePoint = false;
  availableMapStyles = MAP_STYLES;
  currentMapStyle!: MapStyle;
  showMapStyleSelection = false;
  showMapLegend = false;
  legend!: MapIcon[];

  map!: Map;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(
    private readonly mapService: MapService,
    private readonly router: Router,
    private readonly authService: AuthService,
  ) {}

  ngOnInit() {
    this.authService.permissionsLoaded
      .pipe(
        filter((loaded) => loaded),
        take(1),
      )
      .subscribe(() => {
        this.canCreateServicePoint = this.authService.hasPermissionsToCreate(
          ApplicationType.Sepodi,
        );
      });
  }

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    this.getIconsAsImages().then((icons) => (this.legend = icons));
    this.handleMapClick();
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

  routeToNewSP(): void {
    this.router
      .navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path])
      .then()
      .catch((reason) => console.error('Navigation failed:', reason));
  }

  private getIconsAsImages() {
    const types = Object.keys(ServicePointIconType).map((type) => this.getIconAsImage(type));
    return Promise.all(types);
  }

  private getIconAsImage(type: string) {
    const ICONS_BASE_PATH = '../../../../assets/images/service-point-symbols/';

    return new Promise<MapIcon>((resolve) => {
      const image = new Image(20, 20);
      image.addEventListener('load', () => resolve({ id: type, icon: image }));
      image.src = ICONS_BASE_PATH + type + '.svg';
    });
  }
}
