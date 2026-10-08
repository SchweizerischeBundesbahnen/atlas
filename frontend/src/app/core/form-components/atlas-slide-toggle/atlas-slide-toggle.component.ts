import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  output,
  signal,
} from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { Field } from '@angular/forms/signals';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./atlas-slide-toggle.component.scss'],
})
export class AtlasSlideToggleComponent {
  readonly toggle = input(false);
  readonly disabled = input(false);
  readonly slideTrackNeutral = input(false);

  readonly formGroup = input<FormGroup>();
  readonly controlName = input<string>();
  readonly field = input<Field<boolean>>();

  readonly toggleChange = output<boolean>();

  protected readonly currentToggle = signal(false);
  protected readonly isDisabled = computed(() => this.disabled() || (this.field()?.().disabled() ?? false));

  constructor() {
    effect(() => {
      const field = this.field();
      if (field) {
        this.currentToggle.set(field().value());
      }
    });

    effect(() => {
      const toggle = this.toggle();
      if (!this.field()) {
        this.currentToggle.set(toggle);
      }
    });

    effect((onCleanup) => {
      const control = this.formControl;
      if (!control) {
        return;
      }
      this.currentToggle.set(control.value);
      const subscription = control.valueChanges.subscribe((newValue) => this.currentToggle.set(newValue));
      onCleanup(() => subscription.unsubscribe());
    });
  }

  handleToggleClick(): void {
    if (this.isDisabled()) {
      return;
    }
    this.currentToggle.set(!this.currentToggle());
    this.toggleChange.emit(this.currentToggle());

    const field = this.field();
    if (field) {
      field().value.set(this.currentToggle());
      field().markAsDirty();
      field().markAsTouched();
    }
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
