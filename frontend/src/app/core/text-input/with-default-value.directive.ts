import { Directive, HostListener, Input, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({ selector: '[withDefaultValue]' })
export class WithDefaultValueDirective {
  @Input() withDefaultValue!: string;

  constructor(@Self() private ngControl: NgControl) {}

  @HostListener('keyup') onKeyUp() {
    this.fillWithDefaultValue();
  }

  fillWithDefaultValue() {
    if (!this.ngControl.value?.trim()) {
      this.ngControl.reset(this.withDefaultValue);
      this.ngControl.control?.markAsDirty();
    }
  }
}
