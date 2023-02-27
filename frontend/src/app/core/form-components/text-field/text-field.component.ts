import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, FormGroupDirective, ValidationErrors } from '@angular/forms';
import { concat, debounceTime, EMPTY, Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { FieldExample } from './field-example';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
})
export class TextFieldComponent implements OnInit {
  @Input() controlName!: string;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;

  form: FormGroup = new FormGroup({});

  hasError: Observable<boolean> = EMPTY;
  errorTranslationKeyToShow = '';
  errorArgs: ValidationErrors | null | undefined;

  constructor(
    private readonly rootFormGroup: FormGroupDirective,
    private readonly translatePipe: TranslatePipe
  ) {}

  get isTouched(): boolean {
    return !!this.form.get(this.controlName)?.touched;
  }

  get isDirty(): boolean {
    return !!this.form.get(this.controlName)?.dirty;
  }

  ngOnInit() {
    this.form = this.rootFormGroup.control;

    const formControl = this.form.get(this.controlName);
    if (formControl) {
      this.hasError = concat(
        // start value
        of(formControl.invalid),

        // on value changes
        formControl.valueChanges.pipe(
          debounceTime(150),
          map(() => this.hasErrors())
        )
      ).pipe(
        tap((hasError) => {
          if (hasError) {
            this.errorTranslationKeyToShow = this.getFirstErrorTranslationKey();
            this.errorArgs = this.form.get(this.controlName)?.errors;
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
    const validationErrors = this.form.get(this.controlName)?.errors;
    return validationErrors
      ? `VALIDATION_ATLAS_FORM_COMPONENT.${Object.keys(validationErrors)[0].toUpperCase()}`
      : '';
  }

  translate(fieldExample: FieldExample): string {
    if (fieldExample.label && !fieldExample.numberOfChars) {
      return this.translatePipe.transform(fieldExample.label);
    }
    if (fieldExample.label && fieldExample.numberOfChars) {
      return this.translatePipe.transform(fieldExample.label, {
        numberOfChars: fieldExample.numberOfChars,
      });
    }
    return fieldExample.label!;
  }
}
