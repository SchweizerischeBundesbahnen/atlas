import { Injectable, inject } from '@angular/core';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class TranslationSortingService {
  private translatePipe = inject(TranslatePipe);
  public translateService = inject(TranslateService);

  sort(enumsValues: string[], translationPrefix: string): string[] {
    return enumsValues.sort((x, y) => {
      return this.translatePipe.transform(translationPrefix + x) > this.translatePipe.transform(translationPrefix + y)
        ? 1
        : -1;
    });
  }
}
