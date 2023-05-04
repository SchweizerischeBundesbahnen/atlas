import { Pipe, PipeTransform } from '@angular/core';
import { TableColumn } from './table-column';
import { FormatPipe } from './format.pipe';

@Pipe({
  name: 'showTitle',
  pure: true,
})
export class ShowTitlePipe implements PipeTransform {
  private readonly SHOW_TOOLTIP_LENGTH = 20;

  constructor(private readonly formatPipe: FormatPipe) {}

  transform<T>(value: string | Date, column: TableColumn<T>): string {
    const content = this.formatPipe.transform(value, column);
    const hideTooltip = this.hideTooltip(content);
    return !hideTooltip ? content : '';
  }

  private hideTooltip(forText: string | null) {
    if (!forText) {
      return true;
    }
    return forText.length <= this.SHOW_TOOLTIP_LENGTH;
  }
}
