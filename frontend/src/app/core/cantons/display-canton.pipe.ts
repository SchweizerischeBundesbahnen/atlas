import { inject, Pipe, PipeTransform } from '@angular/core';
import { SwissCanton } from '../../api';
import { Cantons } from './Cantons';
import { TranslatePipe } from '@ngx-translate/core';

@Pipe({ name: 'displayCanton' })
export class DisplayCantonPipe implements PipeTransform {
  private readonly translatePipe = inject(TranslatePipe);

  transform(value?: SwissCanton): string {
    if (!value) {
      return '-';
    }
    const translationPath = 'TTH.CANTON.' + Cantons.fromSwissCanton(value)!.short;
    return this.translatePipe.transform(translationPath);
  }
}
