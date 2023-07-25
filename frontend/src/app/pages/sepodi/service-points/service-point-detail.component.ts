import { Component } from '@angular/core';
import { GeoJsonProperties } from 'geojson';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './service-point-detail.component.html',
})
export class ServicePointDetailComponent {
  servicePointClicked($event: GeoJsonProperties) {}
}
