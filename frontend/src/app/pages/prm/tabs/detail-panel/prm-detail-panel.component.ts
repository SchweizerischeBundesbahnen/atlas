import { Component, Input } from '@angular/core';
import { DateRange } from '../../../../core/versioning/date-range';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { SloidContainerComponent } from '../../../../core/sloid-container/sloid-container.component';

@Component({
  selector: 'atlas-prm-detail-panel',
  templateUrl: './prm-detail-panel.component.html',
  imports: [DateRangeTextComponent, SloidContainerComponent],
})
export class PrmDetailPanelComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() isNew = false;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() selectedVersion!: { sloid?: string };
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() maxValidity!: DateRange;
}
