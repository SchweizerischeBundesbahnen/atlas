import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { Router, RouterOutlet } from '@angular/router';
import { Pages } from '../../pages';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { ServicePointSearch } from '../../../core/search-service-point/service-point-search';
import { ApplicationType } from '../../../api';
import { UserService } from '../../../core/auth/user/user.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { SearchServicePointPanelComponent } from '../../../core/search-service-point-panel/search-service-point-panel.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { NgClass } from '@angular/common';
import { MapComponent } from '../map/map.component';

@Component({
  selector: 'app-sepodi-mapview',
  templateUrl: './sepodi-mapview.component.html',
  styleUrls: ['./sepodi-mapview.component.scss'],
  imports: [
    SearchServicePointPanelComponent,
    AtlasButtonComponent,
    NgClass,
    RouterOutlet,
    MapComponent,
  ],
})
export class SepodiMapviewComponent
  implements AfterViewInit, OnDestroy, OnInit
{
  @ViewChild('detailContainer') detailContainer!: ElementRef<HTMLElement>;

  public isSidePanelOpen = false;
  public canCreateServicePoint = false;
  private selectedElementSubscription!: Subscription;
  servicePointSearchType = ServicePointSearch.SePoDi;

  private _showSearchPanel = true;

  get showSearchPanel(): boolean {
    return this._showSearchPanel;
  }

  showPanel() {
    this._showSearchPanel = !this._showSearchPanel;
  }

  constructor(
    private router: Router,
    private mapService: MapService,
    private readonly userService: UserService,
    private readonly permissionService: PermissionService
  ) {
    this.selectedElementSubscription =
      this.mapService.selectedElement.subscribe((selectedPoint) =>
        this.servicePointClicked(selectedPoint)
      );
  }

  ngAfterViewInit() {
    this.styleDetailContainer();
  }

  ngOnDestroy() {
    this.selectedElementSubscription.unsubscribe();
  }

  servicePointClicked($event: GeoJsonProperties) {
    this.router
      .navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number])
      .then();
  }

  setRouteActive(value: boolean) {
    this.isSidePanelOpen = value;
    this.styleDetailContainer();
  }

  private styleDetailContainer() {
    if (this.detailContainer) {
      const detailContainerDiv = this.detailContainer.nativeElement;
      if (this.isSidePanelOpen) {
        detailContainerDiv.classList.add('side-panel-open');
        detailContainerDiv.style.width = '60%';
      } else {
        detailContainerDiv.classList.remove('side-panel-open');
        detailContainerDiv.style.width = 'unset';
      }
    }
  }

  ngOnInit(): void {
    this.userService.onPermissionsLoaded().subscribe(() => {
      this.canCreateServicePoint =
        this.permissionService.hasPermissionsToCreate(ApplicationType.Sepodi);
    });
  }

  routeToNewSP(): void {
    this.router
      .navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path])
      .then()
      .catch((reason) => console.error('Navigation failed: ', reason));
  }
}
