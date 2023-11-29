import { Pipe, PipeTransform } from '@angular/core';
import { ServicePointNumber } from '../../api';

@Pipe({
  name: 'splitServicePointNumber',
})
export class SplitServicePointNumberPipe implements PipeTransform {
  transform(number: string | ServicePointNumber): string {
    if (typeof number === 'string') {
      return this.transformString(number);
    }
    return this.transformString(String(number.number));
  }

  transformString(number: string) {
    if (number.length > 7) {
      const index = this.findIndexToSplit(number);
      return this.splitNumber(number, index);
    }
    return this.splitNumber(number, 1);
  }

  private splitNumber(number: string, index: number): string {
    const uicCountryCode = number.substring(0, index + 1);
    const numberShort = number.substring(index + 1, number.length);
    return uicCountryCode + ' ' + numberShort;
  }

  private isDigit(character: string): boolean {
    const digitExpression = /^\d$/;
    return digitExpression.test(character);
  }

  private findIndexToSplit(number: string): number {
    let currentIndex = -1;
    let foundIndex = 0;
    for (let i = 0; i < number.length; i++) {
      if (this.isDigit(number[i]) && foundIndex < 2) {
        foundIndex++;
        currentIndex = i;
      }
    }
    return currentIndex;
  }
}
