import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { concat, debounceTime, EMPTY, Observable, of } from 'rxjs';
import { FormGroup, ValidationErrors } from '@angular/forms';
import { map, tap } from 'rxjs/operators';
import { AtlasFieldCustomError } from './atlas-field-custom-error';

@Component({
  selector: 'app-atlas-field-error',
  templateUrl: './atlas-field-error.component.html',
  styleUrls: ['./atlas-field-error.component.scss'],
})
export class AtlasFieldErrorComponent implements OnInit {
  @Input() controlName!: string;
  @Input() form: FormGroup = new FormGroup({});
  @Input() customError!: AtlasFieldCustomError;
  hasError: Observable<boolean> = EMPTY;
  errorTranslationKeyToShow = '';
  validationErrors: ValidationErrors | null | undefined;

  constructor(private cdref: ChangeDetectorRef) {}

  get isTouched(): boolean {
    return !!this.form.get(this.controlName)?.touched;
  }

  get isDirty(): boolean {
    return !!this.form.get(this.controlName)?.dirty;
  }

  ngAfterContentChecked() {
    this.cdref.detectChanges();
  }

  ngOnInit() {
    const formControl = this.form.get(this.controlName);
    if (formControl) {
      this.hasError = concat(
        // // start value
        of(formControl.invalid),

        // on value changes
        formControl.valueChanges.pipe(
          debounceTime(300),
          map(() => this.hasErrors())
        )
      ).pipe(
        tap((hasError) => {
          if (hasError) {
            this.errorTranslationKeyToShow = this.getFirstErrorTranslationKey();
          }
        })
      );
    }
  }

  hasErrors(): boolean {
    const formControl = this.form.get(this.controlName);
    return !formControl ? false : formControl.invalid;
  }

  getFirstErrorTranslationKey(): string {
    this.validationErrors = this.form.get(this.controlName)?.errors;

    if (this.customError && this.validationErrors) {
      if (this.validationErrors[this.customError.errorKey]) {
        return this.customError.translationKey;
      }
    }

    return this.validationErrors
      ? `VALIDATION_ATLAS_FORM_COMPONENT.${Object.keys(this.validationErrors)[0].toUpperCase()}`
      : '';
  }
}
