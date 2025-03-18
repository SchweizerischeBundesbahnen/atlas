import {Component, Input} from '@angular/core';
import {DateRange} from "../../../../core/versioning/date-range";
import { NgIf } from '@angular/common';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'prm-detail-panel',
    templateUrl: './prm-detail-panel.component.html',
    imports: [NgIf, DateRangeTextComponent, TranslatePipe]
})
export class PrmDetailPanelComponent {

  @Input() isNew = false;
  @Input() selectedVersion!: { sloid?: string };
  @Input() maxValidity!: DateRange;

}
