import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';

const ITEMS_PER_PAGE = 'PAGINATOR.ITEMS_PER_PAGE';
const NEXT_PAGE = 'PAGINATOR.NEXT_PAGE';
const PREV_PAGE = 'PAGINATOR.PREVIOUS_PAGE';
const RANGE_LABEL1 = 'PAGINATOR.RANGE_LABEL1';
const RANGE_LABEL2 = 'PAGINATOR.RANGE_LABEL2';
@Injectable()
export class TranslatedPaginator extends MatPaginatorIntl {
  constructor(private translate: TranslateService) {
    super();
    this.translateLabels();
    this.translate.onLangChange.subscribe(() => this.translateLabels());
  }

  translateLabels(): void {
    this.translate.get([ITEMS_PER_PAGE, NEXT_PAGE, PREV_PAGE]).subscribe((translation) => {
      this.itemsPerPageLabel = translation[ITEMS_PER_PAGE];
      this.nextPageLabel = translation[NEXT_PAGE];
      this.previousPageLabel = translation[PREV_PAGE];
      this.getRangeLabel = this.getRangeLabel.bind(this);
      this.changes.next();
    });
  }

  getRangeLabel = (page: number, pageSize: number, length: number): string => {
    if (!length || !pageSize) {
      return this.translate.instant(RANGE_LABEL1, { length });
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex =
      startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;
    return this.translate.instant(RANGE_LABEL2, {
      startIndex: startIndex + 1,
      endIndex,
      length,
    });
  };
}
