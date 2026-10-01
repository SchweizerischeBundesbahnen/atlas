import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  OnChanges,
  OnDestroy,
  OnInit,
  output,
  signal,
  SimpleChanges,
} from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { Field } from '@angular/forms/signals';
import { Subscription } from 'rxjs';

@Component({
  selector: 'atlas-slide-toggle',
  templateUrl: './atlas-slide-toggle.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./atlas-slide-toggle.component.scss'],
})
export class AtlasSlideToggleComponent implements OnInit, OnChanges, OnDestroy {
  readonly toggle = input(false);
  readonly disabled = input(false);
  readonly slideTrackNeutral = input(false);

  readonly formGroup = input<FormGroup>();
  readonly controlName = input<string>();
  readonly field = input<Field<boolean>>();

  readonly toggleChange = output<boolean>();

  protected readonly currentToggle = signal(false);
  protected readonly isDisabled = computed(() => this.disabled() || (this.field()?.().disabled() ?? false));
  private formSubscription?: Subscription;

  constructor() {
    effect(() => {
      const field = this.field();
      if (field) {
        this.currentToggle.set(field().value());
      }
    });
  }

  ngOnInit() {
    this.formSubscription?.unsubscribe();
    if (!this.field()) {
      this.currentToggle.set(this.toggle());
    }
    if (this.formControl) {
      this.currentToggle.set(this.formControl.value);
      this.formSubscription = this.formControl.valueChanges.subscribe((newValue) => this.currentToggle.set(newValue));
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

  ngOnDestroy(): void {
    this.formSubscription?.unsubscribe();
  }
}
