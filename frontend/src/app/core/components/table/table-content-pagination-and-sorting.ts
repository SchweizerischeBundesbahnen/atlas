import { TablePagination } from './table-pagination';
import moment from 'moment';

/* eslint-disable  @typescript-eslint/no-explicit-any */
export class TableContentPaginationAndSorting {
  static pageAndSort<TYPE>(
    content: Array<TYPE>,
    settings: TablePagination,
    defaultSort: string,
  ): Array<TYPE> {
    TableContentPaginationAndSorting.sort(content, settings, defaultSort);

    const start = settings.page * settings.size;
    const end = start + settings.size;

    return content.slice(start, end);
  }

  static sort<TYPE>(content: Array<TYPE>, settings: TablePagination, defaultSort: string) {
    content.sort((a: any, b: any) => {
      const sort = settings.sort?.substring(0, settings.sort?.indexOf(',')) || defaultSort;
      const direction = (settings.sort?.substring(settings.sort?.indexOf(',') + 1) || 'asc') as
        | 'asc'
        | 'desc';

      return TableContentPaginationAndSorting.naturalCompare(a[sort], b[sort], direction);
    });
  }

  static naturalCompare(a: any, b: any, direction?: 'asc' | 'desc'): number {
    if (a == null) {
      return -1;
    }
    if (b == null) {
      return 1;
    }

    let result = TableContentPaginationAndSorting.valueCompare(a, b);

    if (direction === 'desc') {
      result = result * -1;
    }
    return result;
  }

  static valueCompare(a: any, b: any): number {
    let result;
    if (moment(a, 'yyyy-MM-DD', true).isValid() && moment(b, 'yyyy-MM-DD', true).isValid()) {
      result = moment(a).isAfter(moment(b)) ? 1 : -1;
    } else if (typeof a === 'string' && typeof b === 'string') {
      result = a.localeCompare(b);
    } else {
      result = a > b ? 1 : -1;
    }
    return result;
  }
}
