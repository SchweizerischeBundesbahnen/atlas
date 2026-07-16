import { Directive, ElementRef, HostListener, inject, input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Directive({ selector: '[atlasInfoLink]' })
export class AtlasInfoLinkDirective {
  private readonly element = inject(ElementRef);
  private readonly translateService = inject(TranslateService);

  readonly infoLinkTranslationKey = input('');

  constructor() {
    this.element.nativeElement.classList.add('atlas-info-link');
  }

  @HostListener('click') onClick() {
    try {
      this.translateService.get(this.infoLinkTranslationKey()).subscribe((link) => {
        if (link === this.infoLinkTranslationKey()) {
          throw new Error('Could not evaluate translationKey correctly');
        }
        window.open(link, '_blank');
      });
    } catch (error) {
      console.error(error);
    }
  }
}
