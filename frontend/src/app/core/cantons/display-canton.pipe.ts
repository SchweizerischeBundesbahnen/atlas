import {Pipe, PipeTransform} from '@angular/core';
import {SwissCanton} from '../../api';
import {Cantons} from './Cantons';

@Pipe({ name: 'transformCantonToShorthand' })
export class TransformCantonToShorthandPipe implements PipeTransform {
  transform(value?: SwissCanton): string {
    if (!value) {
      return '-';
    }
    return 'TTH.CANTON.' + Cantons.fromSwissCanton(value)!.short;
  }
}
