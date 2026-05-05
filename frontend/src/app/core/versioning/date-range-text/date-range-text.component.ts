import { Component, Input } from '@angular/core';
import { DateRange } from '../date-range';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../pipe/display-date.pipe';

@Component({
  selector: 'atlas-date-range-text [dateRange]',
  templateUrl: './date-range-text.component.html',
  imports: [TranslatePipe, DisplayDatePipe],
  providers: [TranslatePipe],
})
export class DateRangeTextComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() dateRange!: DateRange;
}
