import { Directive, ElementRef, HostListener, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Directive({
  selector: '[infoLink]',
})
export class InfoLinkDirective {
  @Input() infoLink = '';

  constructor(
    private readonly element: ElementRef,
    private readonly translateService: TranslateService
  ) {
    this.element.nativeElement.classList.add('atlas-info-link');
  }

  @HostListener('click') onClick() {
    try {
      this.translateService.get(this.infoLink).subscribe((link) => {
        if (link === this.infoLink) {
          throw 'Could not evaluate translationKey correctly';
        }
        window.open(link, '_blank');
      });
    } catch (error) {
      console.error(error);
    }
  }
}
