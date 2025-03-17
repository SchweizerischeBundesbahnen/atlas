import { Pipe, PipeTransform } from '@angular/core';
import { DateService } from '../date/date.service';

@Pipe({
    name: 'displayDate',
    standalone: false
})
export class DisplayDatePipe implements PipeTransform {
  transform(value: Date): string {
    return DateService.getDateFormatted(value);
  }
}
