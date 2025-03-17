import { Component, Input } from '@angular/core';
import { DateRange } from '../date-range';

@Component({
    selector: 'date-range-text [dateRange]',
    templateUrl: './date-range-text.component.html',
    standalone: false
})
export class DateRangeTextComponent {
  @Input() dateRange!: DateRange;
}
