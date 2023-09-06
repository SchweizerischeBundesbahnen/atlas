import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'decimalNumber',
})
export class DecimalNumberPipe implements PipeTransform {
  transform(value: number | undefined, digits: number) {
    if (value) {
      return value.toFixed(digits);
    }
    return undefined;
  }
}
