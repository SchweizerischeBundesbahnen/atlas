import {Component} from '@angular/core';
import {ServicePointSearchType} from "../../sepodi/search-service-point/search-service-point.component";

@Component({
  selector: 'app-prm-search-overview',
  templateUrl: './prm-search-overview.component.html',
  styleUrls: ['./prm-search-overview.component.scss'],
})
export class PrmSearchOverviewComponent {
  protected readonly ServicePointSearchType = ServicePointSearchType;
}
