import { Directive, HostListener, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({ selector: '[trim]' })
export class TrimInputDirective {
  constructor(@Self() private ngControl: NgControl) {}

  @HostListener('keyup') onKeyUp() {
    this.ngControl.control?.setValue(this.ngControl.value?.trim());
  }
}
