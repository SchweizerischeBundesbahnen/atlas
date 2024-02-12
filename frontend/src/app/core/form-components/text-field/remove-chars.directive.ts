import {AfterViewInit, Directive, ElementRef, HostListener, Input, Self} from '@angular/core';

@Directive({selector: '[removeChars]'})
export class RemoveCharsDirective implements AfterViewInit {
  @Input() removeChars: string[] = [];

  private inputElement?: HTMLInputElement;
  private value?: string;

  constructor(@Self() private element: ElementRef) {
  }

  ngAfterViewInit() {
    this.inputElement = this.element.nativeElement.querySelector('input');
    this.value = this.inputElement?.value;
  }

  @HostListener('keyup') onKeyUp() {
    if (this.inputElement) {
      if (this.value !== this.inputElement.value) {
        for (const charToRemove of this.removeChars) {
          this.inputElement.value = this.inputElement.value.replace(charToRemove, '');
        }
        this.inputElement.dispatchEvent(new Event('input'));
      }
      this.value = this.inputElement.value;
    }
  }
}
