import { Component, output, input } from '@angular/core';
import { ServicePointSearchType } from '../search-service-point/service-point-search';
import { NgClass } from '@angular/common';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSlideToggleComponent } from '../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { NavigateServicePointComponent } from '../navigate-service-point/navigate-service-point.component';
import { SearchServicePointComponent } from '../search-service-point/search-service-point.component';

export type SearchNavigationType = 'SEARCH' | 'NAVIGATION';
export const SearchNavigationType = {
  Search: 'SEARCH' as SearchNavigationType,
  Navigation: 'NAVIGATION' as SearchNavigationType,
};

@Component({
  selector: 'atlas-search-service-point-panel',
  templateUrl: './search-service-point-panel.component.html',
  styleUrls: ['./search-service-point-panel.component.scss'],
  imports: [
    NgClass,
    AtlasButtonComponent,
    TranslatePipe,
    AtlasSlideToggleComponent,
    NavigateServicePointComponent,
    SearchServicePointComponent,
  ],
})
export class SearchServicePointPanelComponent {
  readonly searchType = input.required<ServicePointSearchType>();
  readonly toggleEvent = output<void>();

  private _showSearchPanel = true;
  private _searchNavigationType: SearchNavigationType = 'SEARCH';

  get showSearchPanel() {
    return this._showSearchPanel;
  }

  get searchNavigationType(): SearchNavigationType {
    return this._searchNavigationType;
  }

  set searchNavigationType(value: SearchNavigationType) {
    this._searchNavigationType = value;
  }

  toggle() {
    this.toggleEvent.emit();
    this._showSearchPanel = !this.showSearchPanel;
  }

  changeSearchType(changeSearch: boolean) {
    this.searchNavigationType = changeSearch ? 'NAVIGATION' : 'SEARCH';
  }
}
