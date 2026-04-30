import { AfterViewInit, Directive, ElementRef, HostListener, inject, Input } from '@angular/core';

@Directive({ selector: '[atlasRemoveChars]' })
export class RemoveCharsDirective implements AfterViewInit {
  @Input() atlasRemoveChars: string[] = [];

  private inputElement?: HTMLInputElement;
  private value?: string;

  private readonly element = inject(ElementRef, { self: true });

  ngAfterViewInit() {
    this.inputElement = this.element.nativeElement.querySelector('input');
    this.value = this.inputElement?.value;
  }

  @HostListener('keyup') onKeyUp() {
    if (this.inputElement) {
      if (this.value !== this.inputElement.value) {
        for (const charToRemove of this.atlasRemoveChars) {
          this.inputElement.value = this.inputElement.value.replace(charToRemove, '');
        }
        this.inputElement.dispatchEvent(new Event('input'));
      }
      this.value = this.inputElement.value;
    }
  }
}
