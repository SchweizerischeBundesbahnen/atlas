import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {GeoJsonProperties} from 'geojson';
import {Router} from '@angular/router';
import {Pages} from '../../pages';
import {MapService} from '../map/map.service';
import {Subscription, take} from 'rxjs';
import {ServicePointSearch} from "../../../core/search-service-point/service-point-search";
import {filter} from "rxjs/operators";
import {ApplicationType} from "../../../api";
import {UserService} from "../../../core/auth/user/user.service";
import {PermissionService} from "../../../core/auth/permission/permission.service";

@Component({
  selector: 'app-sepodi-mapview',
  templateUrl: './sepodi-mapview.component.html',
  styleUrls: ['./sepodi-mapview.component.scss'],
})
export class SepodiMapviewComponent implements AfterViewInit, OnDestroy, OnInit {
  @ViewChild('detailContainer') detailContainer!: ElementRef<HTMLElement>;

  public isSidePanelOpen = false;
  public canCreateServicePoint = false;
  private selectedElementSubscription!: Subscription;
  servicePointSearchType = ServicePointSearch.SePoDi;

  private _showSearchPanel = true;

  get showSearchPanel(): boolean {
    return this._showSearchPanel;
  }

  showPanel(value: boolean) {
    this._showSearchPanel = !this._showSearchPanel;
  }


  constructor(
    private router: Router,
    private mapService: MapService,
    private readonly userService: UserService,
    private readonly permissionService: PermissionService,
  ) {
    this.selectedElementSubscription = this.mapService.selectedElement.subscribe((selectedPoint) =>
      this.servicePointClicked(selectedPoint),
    );
  }

  ngAfterViewInit() {
    this.styleDetailContainer();
  }

  ngOnDestroy() {
    this.selectedElementSubscription.unsubscribe();
  }

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number]).then();
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
    this.userService.permissionsLoaded
      .pipe(
        filter((loaded) => loaded),
        take(1),
      )
      .subscribe(() => {
        this.canCreateServicePoint = this.permissionService.hasPermissionsToCreate(
          ApplicationType.Sepodi,
        );
      });
  }

  routeToNewSP(): void {
    this.router
      .navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path])
      .then()
      .catch((reason) => console.error('Navigation failed: ', reason));
  }
}
