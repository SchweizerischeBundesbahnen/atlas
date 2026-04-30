import { AfterViewInit, Directive, ElementRef, inject } from '@angular/core';

@Directive({ selector: '[atlasScrollToTop]' })
export class ScrollToTopDirective implements AfterViewInit {
  private readonly elementRef = inject(ElementRef, { self: true });

  ngAfterViewInit() {
    const scrollbarElement = this.elementRef.nativeElement.closest('#scrollbar-content-container');
    scrollbarElement?.scroll(0, 0);
  }
}
