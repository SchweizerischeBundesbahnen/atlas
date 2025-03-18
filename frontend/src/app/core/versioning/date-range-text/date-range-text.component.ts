import { Component, Input } from '@angular/core';
import { DateRange } from '../date-range';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../pipe/display-date.pipe';

@Component({
    selector: 'date-range-text [dateRange]',
    templateUrl: './date-range-text.component.html',
    imports: [TranslatePipe, DisplayDatePipe]
})
export class DateRangeTextComponent {
  @Input() dateRange!: DateRange;
}
