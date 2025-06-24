import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { NgClass } from '@angular/common';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  styleUrls: ['./atlas-slide-toggle.component.scss'],
  imports: [NgClass],
})
export class AtlasSlideToggleComponent implements OnInit {
  @Input() toggle = false;
  @Input() disabled = false;

  @Input() formGroup?: FormGroup;
  @Input() controlName?: string;

  @Output() toggleChange = new EventEmitter<boolean>();

  ngOnInit() {
    if (this.formControl) {
      this.toggle = this.formControl.value;
      this.formControl.valueChanges.subscribe(
        (newValue) => (this.toggle = newValue)
      );
    }
  }

  handleToggleClick(): void {
    this.toggle = !this.toggle;
    this.toggleChange.emit(this.toggle);

    if (this.formControl) {
      this.formControl?.setValue(this.toggle);
    }
  }

  get formControl(): AbstractControl | null | undefined {
    if (this.controlName) {
      return this.formGroup?.get(this.controlName);
    } else {
      return undefined;
    }
  }
}
