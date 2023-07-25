import { Component } from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
})
export class SepodiOverviewComponent {
  constructor(private router: Router, private route: ActivatedRoute) {}

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SERVICE_POINTS.path, $event!.number]).then();
  }
}
