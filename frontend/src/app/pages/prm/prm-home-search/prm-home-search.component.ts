import { Component } from '@angular/core';
import { ServicePointSearchType } from '../../sepodi/search-service-point/search-service-point.component';

@Component({
  selector: 'app-prm-home-search',
  templateUrl: './prm-home-search.component.html',
  styleUrls: ['./prm-home-search.component.scss'],
})
export class PrmHomeSearchComponent {
  protected readonly ServicePointSearchType = ServicePointSearchType;
}
