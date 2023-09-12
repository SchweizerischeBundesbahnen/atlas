import { Injectable } from '@angular/core';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class TranslationSortingService {
  constructor(private translatePipe: TranslatePipe, public translateService: TranslateService) {}

  sort(enumsValues: string[], translationPrefix: string): string[] {
    return enumsValues.sort((x, y) => {
      return this.translatePipe.transform(translationPrefix + x) >
        this.translatePipe.transform(translationPrefix + y)
        ? 1
        : -1;
    });
  }
}
