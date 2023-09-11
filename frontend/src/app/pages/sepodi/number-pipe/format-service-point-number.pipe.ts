import { Pipe, PipeTransform } from '@angular/core';
import { ServicePointNumber } from '../../../api';
import { SplitServicePointNumberPipe } from '../search-service-point/split-service-point-number.pipe';

@Pipe({
  name: 'formatServicePointNumber',
})
export class FormatServicePointNumber implements PipeTransform {
  transform(value: ServicePointNumber): string {
    return new SplitServicePointNumberPipe().transform(String(value.number));
  }
}
