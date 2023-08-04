import { Pipe, PipeTransform } from '@angular/core';
import { ServicePointNumber } from '../../../api';

@Pipe({
  name: 'formatServicePointNumber',
})
export class FormatServicePointNumber implements PipeTransform {
  transform(value: ServicePointNumber): string {
    return `${value.uicCountryCode} ${value.numberShort}`;
  }
}
