import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { GeoJsonProperties } from 'geojson';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent {
  constructor(private router: Router) {}

  servicePointClicked($event: GeoJsonProperties) {
    this.router.navigate([Pages.SEPODI.path, Pages.SERVICE_POINTS.path, $event!.number]).then();
  }
}
