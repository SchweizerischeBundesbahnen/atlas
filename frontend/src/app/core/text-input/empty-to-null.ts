import { Directive, HostListener, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({ selector: '[emptyToNull]' })
export class EmptyToNullDirective {
  constructor(@Self() private ngControl: NgControl) {}

  @HostListener('keyup') onKeyUp() {
    if (this.ngControl.value?.trim() === '') {
      this.ngControl.reset(null);
      this.ngControl.control?.markAsDirty();
    }
  }
}
