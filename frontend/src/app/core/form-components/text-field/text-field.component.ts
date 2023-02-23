import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, FormGroupDirective } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'atlas-text-field',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
})
export class TextFieldComponent implements OnInit {
  @Input() controlName!: string;

  form: FormGroup = new FormGroup({});

  hasError: Observable<boolean> = of(false);
  errorTranslationKeyToShow: Observable<string> = of('');

  constructor(private rootFormGroup: FormGroupDirective) {}

  ngOnInit() {
    this.form = this.rootFormGroup.control;

    const formControl = this.form.get(this.controlName);
    if (formControl) {
      this.hasError = formControl.valueChanges.pipe(
        map(() => {
          return !this.hideError();
        })
      );
      this.errorTranslationKeyToShow = this.hasError.pipe(
        map((hasError) => {
          if (hasError) {
            return this.getFirstErrorTranslationKey();
          }
          return '';
        })
      );
    }
  }

  hideError(): boolean {
    const formControl = this.form.get(this.controlName);
    return !formControl ? true : formControl.valid || formControl.pristine;
  }

  getFirstErrorTranslationKey(): string {
    console.log('test');
    const validationErrors = this.form.get(this.controlName)?.errors;
    if (!validationErrors) {
      return '';
    }
    return `VALIDATION_ATLAS_FORM_COMPONENT.${Object.keys(validationErrors)[0].toUpperCase()}`;
  }
}
