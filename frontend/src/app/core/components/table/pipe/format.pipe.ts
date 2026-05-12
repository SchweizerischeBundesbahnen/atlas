import { inject, Pipe, PipeTransform } from '@angular/core';
import { TableColumn } from '../table-column';
import { DateService } from '../../../date/date.service';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'format',
  pure: false,
})
export class FormatPipe implements PipeTransform {
  private readonly translate = inject(TranslateService);

  transform<T>(value: string | Date | undefined, column: TableColumn<T>): string {
    if (value === undefined || value === null) {
      return '';
    }
    if (column.formatAsDate) {
      return DateService.getDateFormatted(value as Date);
    }
    if (column.translate?.withPrefix) {
      return this.translate.instant(column.translate.withPrefix + value);
    }
    if (column.callback) {
      return column.callback(value);
    }
    return value as string;
  }
}
