import { AfterViewInit, Directive, ElementRef, HostListener, Input, Self } from '@angular/core';

@Directive({ selector: '[removeChars]' })
export class RemoveCharsDirective implements AfterViewInit {
  @Input() removeChars: string[] = [];

  private inputElement?: HTMLInputElement;

  constructor(@Self() private element: ElementRef) {}

  ngAfterViewInit() {
    this.inputElement = this.element.nativeElement.querySelector('input');
  }

  @HostListener('keyup') onKeyUp() {
    if (this.inputElement) {
      this.inputElement.value = this.inputElement.value.replace("'", '');
      this.inputElement.dispatchEvent(new Event('input'));
    }
  }
}
