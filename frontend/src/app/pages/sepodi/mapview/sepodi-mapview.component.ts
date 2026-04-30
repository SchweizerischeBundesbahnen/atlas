import { AfterViewInit, Component, ElementRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { Router, RouterOutlet } from '@angular/router';
import { Pages } from '../../pages';
import { MapService } from '../map/map.service';
import { Subscription } from 'rxjs';
import { ServicePointSearch } from '../../../core/search-service-point/service-point-search';
import { SearchServicePointPanelComponent } from '../../../core/search-service-point-panel/search-service-point-panel.component';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { NgClass } from '@angular/common';
import { MapComponent } from '../map/map.component';

@Component({
  selector: 'atlas-sepodi-mapview',
  templateUrl: './sepodi-mapview.component.html',
  styleUrls: ['./sepodi-mapview.component.scss'],
  imports: [AtlasButtonComponent, NgClass, RouterOutlet, MapComponent, SearchServicePointPanelComponent],
})
export class SepodiMapviewComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly router = inject(Router);
  private readonly mapService = inject(MapService);

  @ViewChild('detailContainer') detailContainer!: ElementRef<HTMLElement>;

  protected isSidePanelOpen = false;
  protected readonly servicePointSearchType = ServicePointSearch.SePoDi;
  private selectedElementSubscription!: Subscription;
  private _showSearchPanel = true;

  ngOnInit(): void {
    this.selectedElementSubscription = this.mapService.selectedElement.subscribe((selectedPoint) =>
      this.servicePointClicked(selectedPoint)
    );
  }

  ngAfterViewInit() {
    this.styleDetailContainer();
  }

  ngOnDestroy() {
    this.selectedElementSubscription.unsubscribe();
  }

  get showSearchPanel(): boolean {
    return this._showSearchPanel;
  }

  showPanel() {
    this._showSearchPanel = !this._showSearchPanel;
  }

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number]).then();
  }

  setRouteActive(value: boolean) {
    this.isSidePanelOpen = value;
    this.styleDetailContainer();
  }

  routeToNewSP(): void {
    this.router
      .navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path])
      .then()
      .catch((reason) => console.error('Navigation failed: ', reason));
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
}
