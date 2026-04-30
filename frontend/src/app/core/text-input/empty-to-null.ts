import { Directive, HostListener, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({ selector: '[atlasEmptyToNull]' })
export class EmptyToNullDirective {
  private ngControl = inject(NgControl, { self: true });

  @HostListener('keyup') onKeyUp() {
    if (typeof this.ngControl.value === 'string' && this.ngControl.value?.trim() === '') {
      this.ngControl.reset(null);
      this.ngControl.control?.markAsDirty();
    }
  }
}
