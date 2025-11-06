import { Component, inject, OnInit } from '@angular/core';
import { ServicePointSearch } from '../../../core/search-service-point/service-point-search';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { SearchServicePointPanelComponent } from '../../../core/search-service-point-panel/search-service-point-panel.component';
import { NgClass } from '@angular/common';
import { PrmInfoBoxComponent } from './prm-info-box/prm-info-box.component';

@Component({
  selector: 'app-prm-home-search',
  templateUrl: './prm-home-search.component.html',
  styleUrls: ['./prm-home-search.component.scss'],
  imports: [
    SearchServicePointPanelComponent,
    PrmInfoBoxComponent,
    NgClass,
    RouterOutlet,
  ],
})
export class PrmHomeSearchComponent implements OnInit {
  servicePointSearch = ServicePointSearch.PRM;
  isPrmHome = false;

  private _showSearchPanel = true;
  private readonly route = inject(ActivatedRoute);

  get showSearchPanel(): boolean {
    return this._showSearchPanel;
  }

  showPanel() {
    this._showSearchPanel = !this._showSearchPanel;
  }

  ngOnInit() {
    this.isPrmHome = this.route.snapshot.data.isHome;
  }
}
