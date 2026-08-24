import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
  input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { LngLatLike, Map } from 'maplibre-gl';
import { MapService } from './map.service';
import { MAP_STYLES, MapStyle } from './map-options';
import { Subject } from 'rxjs';
import { ApplicationType, BusinessOrganisation } from '../../../api';
import { takeUntil } from 'rxjs/operators';
import { MapIcon, MapIconsService } from './map-icons.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { UserService } from '../../../core/auth/user/user.service';
import { SERVICE_POINT_MIN_ZOOM } from './map-style';
import { NgClass } from '@angular/common';
import { MatIconButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { MapBoFilterDialogComponent } from './map-bo-filter-dialog/map-bo-filter-dialog.component';
import { MapBoFilterDialogData } from './map-bo-filter-dialog/map-bo-filter-dialog-data';

@Component({
  selector: 'atlas-map',
  templateUrl: './map.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./map.component.scss'],
  imports: [MatIconButton, NgClass, TranslatePipe],
})
export class MapComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly mapService = inject(MapService);
  private readonly userService = inject(UserService);
  private readonly permissionService = inject(PermissionService);
  private readonly dialogService = inject(DialogService);

  public readonly isSidePanelOpen = input(false);

  public canCreateServicePoint = false;
  servicePointsShown = false;
  availableMapStyles = MAP_STYLES;
  currentMapStyle!: MapStyle;
  showMapStyleSelection = false;
  showMapLegend = false;
  legend!: MapIcon[];

  map!: Map;

  private readonly onDestroy$ = new Subject<boolean>();

  @ViewChild('map')
  private readonly mapContainer!: ElementRef<HTMLElement>;

  ngOnInit() {
    this.userService.onPermissionsLoaded().subscribe(() => {
      this.canCreateServicePoint = this.permissionService.hasPermissionsToCreate(ApplicationType.Sepodi);
    });
    this.mapService.servicePointsShown
      .pipe(takeUntil(this.onDestroy$))
      .subscribe((value) => (this.servicePointsShown = value));
  }

  ngAfterViewInit() {
    this.map = this.mapService.initMap(this.mapContainer.nativeElement);
    this.currentMapStyle = this.mapService.currentMapStyle;
    MapIconsService.getLegendIconsAsImages().then((icons) => (this.legend = icons));
  }

  ngOnDestroy() {
    this.mapService.removeMap();
    this.mapService.mapInitialized.next(false);
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
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

  zoomToServicePointMin() {
    this.map.zoomTo(SERVICE_POINT_MIN_ZOOM, { duration: 500 });
  }

  goHome() {
    const swissLongLat = [8.2275, 46.8182];
    this.map.flyTo({
      center: swissLongLat as LngLatLike,
      zoom: 7.25,
      speed: 0.8,
    });
  }

  openBoFilterDialog() {
    const dialogData: MapBoFilterDialogData = {
      title: 'SEPODI.MAP_BO_FILTER.TITLE',
      message: '',
      businessOrganisations: [...this.mapService.boFilter()],
    };
    this.dialogService
      .openDialogDataWithCustomResult<MapBoFilterDialogData, BusinessOrganisation[]>(
        dialogData,
        MapBoFilterDialogComponent
      )
      .subscribe((result) => {
        if (result) {
          this.mapService.applyBoFilter(result);
        }
      });
  }

  boFilterActive(): boolean {
    return this.mapService.boFilterActive();
  }

  boFilterCount(): number {
    return this.mapService.boFilter().length;
  }

  boFilterLabel(): string {
    return this.boFilterActive() ? 'SEPODI.MAP_BO_FILTER.TOOLTIP_ACTIVE' : 'SEPODI.MAP_BO_FILTER.TOOLTIP';
  }
}
