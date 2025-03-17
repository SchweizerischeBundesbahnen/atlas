import { Pipe, PipeTransform } from '@angular/core';
import { Line } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Pipe({
    name: 'mainlineDescription',
    pure: true,
    standalone: false
})
export class MainlineDescriptionPipe implements PipeTransform {
  constructor(private readonly translatePipe: TranslatePipe) {}

  transform(value: Line): string {
    let desc = value.description;
    if (!desc) {
      desc = `(${this.translatePipe.transform('LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE')})`;
    }
    return `${desc}`;
  }
}
