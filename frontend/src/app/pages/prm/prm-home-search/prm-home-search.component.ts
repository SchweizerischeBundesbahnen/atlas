import {Component} from '@angular/core';
import {ServicePointSearch} from '../../../core/search-service-point/service-point-search';
import {NavigationEnd, Router} from "@angular/router";
import {filter} from "rxjs/operators";
import {Pages} from "../../pages";

@Component({
  selector: 'app-prm-home-search',
  templateUrl: './prm-home-search.component.html',
  styleUrls: ['./prm-home-search.component.scss'],
})
export class PrmHomeSearchComponent {
  servicePointSearch = ServicePointSearch.PRM;

  private _isPrmHome = true;

  get isPrmHome(): boolean {
    return this._isPrmHome;
  }

  constructor(private router: Router) {
    this.navigationEvent();
  }

  navigationEvent() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this._isPrmHome = event.url === '/' + Pages.PRM.path;
    });
  }
}
