import {
  ChangeDetectionStrategy,
  Component,
  input,
  OnChanges,
  OnInit,
  output,
  signal,
  SimpleChanges,
} from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./atlas-slide-toggle.component.scss'],
})
export class AtlasSlideToggleComponent implements OnInit, OnChanges {
  readonly toggle = input(false);
  readonly disabled = input(false);
  readonly slideTrackNeutral = input(false);

  readonly formGroup = input<FormGroup>();
  readonly controlName = input<string>();

  readonly toggleChange = output<boolean>();

  protected readonly currentToggle = signal(false);

  ngOnInit() {
    this.currentToggle.set(this.toggle());
    if (this.formControl) {
      this.currentToggle.set(this.formControl.value);
      this.formControl.valueChanges.subscribe((newValue) => this.currentToggle.set(newValue));
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.toggle) {
      this.currentToggle.set(this.toggle());
    }
    if (changes.formGroup || changes.controlName) {
      this.ngOnInit();
    }
  }

  handleToggleClick(): void {
    this.currentToggle.set(!this.currentToggle());
    this.toggleChange.emit(this.currentToggle());

    if (this.formControl) {
      this.formControl?.setValue(this.currentToggle());
      this.formControl?.markAsDirty();
    }
  }

  get formControl(): AbstractControl | null | undefined {
    const controlName = this.controlName();
    if (controlName) {
      return this.formGroup()?.get(controlName);
    } else {
      return undefined;
    }
  }
}
