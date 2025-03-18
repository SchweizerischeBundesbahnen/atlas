import {Component} from '@angular/core';
import {ServicePointSearch} from '../../../core/search-service-point/service-point-search';
import { NavigationEnd, Router, RouterOutlet } from "@angular/router";
import {filter} from "rxjs/operators";
import {Pages} from "../../pages";
import { SearchServicePointPanelComponent } from '../../../core/search-service-point-panel/search-service-point-panel.component';
import { NgIf, NgClass } from '@angular/common';
import { PrmInfoBoxComponent } from './prm-info-box/prm-info-box.component';

@Component({
    selector: 'app-prm-home-search',
    templateUrl: './prm-home-search.component.html',
    styleUrls: ['./prm-home-search.component.scss'],
    imports: [SearchServicePointPanelComponent, NgIf, PrmInfoBoxComponent, NgClass, RouterOutlet]
})
export class PrmHomeSearchComponent {
  servicePointSearch = ServicePointSearch.PRM;
  private _showSearchPanel = true;
  private _isPrmHome = true;

  get isPrmHome(): boolean {
    return this._isPrmHome;
  }

  get showSearchPanel(): boolean {
    return this._showSearchPanel;
  }

  showPanel() {
    this._showSearchPanel = !this._showSearchPanel;
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
