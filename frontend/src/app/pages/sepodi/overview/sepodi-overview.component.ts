import { Component } from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { Router } from '@angular/router';
import { Pages } from '../../pages';
import { MapService } from '../map/map.service';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent {
  constructor(private router: Router, private mapService: MapService) {
    this.mapService.selectedElement.subscribe((selectedPoint) =>
      this.servicePointClicked(selectedPoint)
    );
  }

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number]).then();
  }
}
