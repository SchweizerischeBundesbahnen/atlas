import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ServicePointSearchType} from "../search-service-point/service-point-search";

@Component({
  selector: 'app-search-service-point-panel',
  templateUrl: './search-service-point-panel.component.html',
  styleUrls: ['./search-service-point-panel.component.scss']
})
export class SearchServicePointPanelComponent {

  @Input() searchType!: ServicePointSearchType;
  @Output() toggleEvent = new EventEmitter<boolean>();

  private _showSearchPanel = true;

  get showSearchPanel(){
    return this._showSearchPanel;
  }

  toggle() {
    this.toggleEvent.emit();
    this._showSearchPanel = !this.showSearchPanel;
    console.log('asdasd')
  }



}
