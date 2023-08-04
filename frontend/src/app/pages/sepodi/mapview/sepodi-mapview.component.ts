import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { Router } from '@angular/router';
import { Pages } from '../../pages';
import { MapService } from '../map/map.service';

@Component({
  selector: 'app-sepodi-mapview',
  templateUrl: './sepodi-mapview.component.html',
  styleUrls: ['./sepodi-mapview.component.scss'],
})
export class SepodiMapviewComponent implements AfterViewInit {
  @ViewChild('detailContainer') detailContainer!: ElementRef<HTMLElement>;

  private routeActive = false;

  constructor(private router: Router, private mapService: MapService) {
    this.mapService.selectedElement.subscribe((selectedPoint) =>
      this.servicePointClicked(selectedPoint)
    );
  }

  ngAfterViewInit() {
    this.styleDetailContainer();
  }

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number]).then();
  }

  setRouteActive(value: boolean) {
    this.routeActive = value;
    this.styleDetailContainer();
  }

  private styleDetailContainer() {
    if (this.detailContainer) {
      const detailContainerDiv = this.detailContainer.nativeElement;
      if (this.routeActive) {
        detailContainerDiv.classList.add('side-panel-open');
      } else {
        detailContainerDiv.classList.remove('side-panel-open');
        detailContainerDiv.style.width = 'unset';
      }
    }
  }
}
