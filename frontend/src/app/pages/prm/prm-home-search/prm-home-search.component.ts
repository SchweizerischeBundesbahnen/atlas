import { Component } from '@angular/core';
import { ServicePointSearch } from '../../../core/search-service-point/service-point-search';

@Component({
  selector: 'app-prm-home-search',
  templateUrl: './prm-home-search.component.html',
  styleUrls: ['./prm-home-search.component.scss'],
})
export class PrmHomeSearchComponent {
  servicePointSearch = ServicePointSearch.PRM;
}
