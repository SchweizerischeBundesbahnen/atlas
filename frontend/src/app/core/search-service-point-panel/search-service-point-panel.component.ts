import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ServicePointSearchType } from '../search-service-point/service-point-search';
import { NgClass, NgIf } from '@angular/common';
import { SearchServicePointComponent } from '../search-service-point/search-service-point.component';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-search-service-point-panel',
  templateUrl: './search-service-point-panel.component.html',
  styleUrls: ['./search-service-point-panel.component.scss'],
  imports: [
    NgClass,
    NgIf,
    SearchServicePointComponent,
    AtlasButtonComponent,
    TranslatePipe,
  ],
})
export class SearchServicePointPanelComponent {
  @Input() searchType!: ServicePointSearchType;
  @Output() toggleEvent = new EventEmitter<boolean>();

  private _showSearchPanel = true;

  get showSearchPanel() {
    return this._showSearchPanel;
  }

  toggle() {
    this.toggleEvent.emit();
    this._showSearchPanel = !this.showSearchPanel;
  }
}
