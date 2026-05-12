import { Pipe, PipeTransform, inject } from '@angular/core';
import { Line } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Pipe({
  name: 'mainlineDescription',
  pure: true,
})
export class MainlineDescriptionPipe implements PipeTransform {
  private readonly translatePipe = inject(TranslatePipe);

  transform(value: Line): string {
    let desc = value.description;
    if (!desc) {
      desc = `(${this.translatePipe.transform('LIDI.SUBLINE.NO_LINE_DESIGNATION_AVAILABLE')})`;
    }
    return `${desc}`;
  }
}
