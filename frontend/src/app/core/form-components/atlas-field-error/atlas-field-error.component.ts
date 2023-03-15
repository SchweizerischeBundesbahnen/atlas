import { Component, Input } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AtlasFieldCustomError } from './atlas-field-custom-error';
import { ValidationService } from '../../validation/validation.service';

@Component({
  selector: 'app-atlas-field-error',
  templateUrl: './atlas-field-error.component.html',
  styleUrls: ['./atlas-field-error.component.scss'],
})
export class AtlasFieldErrorComponent {
  @Input() controlName!: string;
  @Input() form: FormGroup = new FormGroup({});
  @Input() control!: FormControl;
  @Input() customError!: AtlasFieldCustomError;

  constructor(private validationService: ValidationService) {}

  get errors() {
    if (this.control) {
      return this.validationService.getValidation(this.control.errors || null);
    } else {
      const formField = this.form.get(this.controlName);
      const validationErrors = this.getValidationErrors();
      if (validationErrors) {
        if ((validationErrors['required'] && formField?.touched) || !validationErrors['required']) {
          return this.validationService.getValidation(validationErrors || null);
        }
      }
    }
    return null;
  }

  get error() {
    const validationErrors = this.getValidationErrors();
    if (validationErrors) {
      if (this.customError && validationErrors[this.customError.errorKey]) {
        return this.customError.translationKey;
      }
    }
    return null;
  }

  private getValidationErrors() {
    const formField = this.form.get(this.controlName);
    return formField?.errors;
  }
}
