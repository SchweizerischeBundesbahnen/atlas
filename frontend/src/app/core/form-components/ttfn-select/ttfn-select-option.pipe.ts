import { Pipe, PipeTransform } from '@angular/core';
import { TimetableFieldNumber } from '../../../api';

@Pipe({
  name: 'ttfnSelectDisplay',
  pure: true,
})
export class TimetableFieldNumberSelectOptionPipe implements PipeTransform {
  transform(value: TimetableFieldNumber): string {
    return `${value.swissTimetableFieldNumber} - ${value.number}`;
  }
}
