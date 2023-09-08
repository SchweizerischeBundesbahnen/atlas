import { Pipe, PipeTransform } from '@angular/core';
import { ServicePointNumber } from '../../../api';

@Pipe({
  name: 'formatServicePointNumber',
})
export class FormatServicePointNumber implements PipeTransform {
  transform(value: ServicePointNumber): string {
    let numberShort = String(value.numberShort);
    numberShort = '0'.repeat(5 - numberShort.length) + numberShort;
    return `${value.uicCountryCode} ${numberShort}`;
  }
}
