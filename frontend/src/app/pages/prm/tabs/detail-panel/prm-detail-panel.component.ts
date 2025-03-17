import {Component, Input} from '@angular/core';
import {DateRange} from "../../../../core/versioning/date-range";

@Component({
    selector: 'prm-detail-panel',
    templateUrl: './prm-detail-panel.component.html',
    standalone: false
})
export class PrmDetailPanelComponent {

  @Input() isNew = false;
  @Input() selectedVersion!: { sloid?: string };
  @Input() maxValidity!: DateRange;

}
