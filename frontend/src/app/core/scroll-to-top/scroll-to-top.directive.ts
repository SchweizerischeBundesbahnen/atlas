import { AfterViewInit, Directive, ElementRef, Self } from '@angular/core';

@Directive({ selector: '[scrollToTop]' })
export class ScrollToTopDirective implements AfterViewInit {
  constructor(@Self() private elementRef: ElementRef<HTMLElement>) {}

  ngAfterViewInit() {
    const scrollbarElement = this.elementRef.nativeElement.closest(
      '#scrollbar-content-container'
    );
    scrollbarElement?.scroll(0, 0);
  }
}
