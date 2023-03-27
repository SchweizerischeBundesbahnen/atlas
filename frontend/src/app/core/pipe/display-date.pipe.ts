import { Pipe, PipeTransform } from '@angular/core';
import { DateService } from '../date/date.service';

@Pipe({
  name: 'displayDate',
})
export class DisplayDatePipe implements PipeTransform {
  transform(value: Date): unknown {
    return DateService.getDateFormatted(value);
  }
}
