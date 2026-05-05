import { Component, Input, OnChanges, OnInit, SimpleChanges, output, input } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  styleUrls: ['./atlas-slide-toggle.component.scss'],
})
export class AtlasSlideToggleComponent implements OnInit, OnChanges {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() toggle = false;
  readonly disabled = input(false);
  readonly slideTrackNeutral = input(false);

  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup?: FormGroup;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() controlName?: string;

  readonly toggleChange = output<boolean>();

  ngOnInit() {
    if (this.formControl) {
      this.toggle = this.formControl.value;
      this.formControl.valueChanges.subscribe((newValue) => (this.toggle = newValue));
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.formGroup) {
      this.ngOnInit();
    }
  }

  handleToggleClick(): void {
    this.toggle = !this.toggle;
    this.toggleChange.emit(this.toggle);

    if (this.formControl) {
      this.formControl?.setValue(this.toggle);
      this.formControl?.markAsDirty();
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
