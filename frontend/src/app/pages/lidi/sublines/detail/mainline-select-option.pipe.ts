import { Pipe, PipeTransform } from '@angular/core';
import { Line } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Pipe({
  name: 'mainlineSelectOption',
  pure: true,
})
export class MainlineSelectOptionPipe implements PipeTransform {
  constructor(private readonly translatePipe: TranslatePipe) {}

  transform(value: Line, ...args: unknown[]): string {
    let desc = value.description;
    if (!desc) {
      desc = `(${this.translatePipe.transform('LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE')})`;
    }
    return `${value.swissLineNumber} ${desc}`;
  }
}
