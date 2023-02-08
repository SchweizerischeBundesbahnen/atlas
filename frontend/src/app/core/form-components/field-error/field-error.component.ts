import { Component, Injector } from '@angular/core';
import { MatLegacyFormField as MatFormField } from '@angular/material/legacy-form-field';
import { ValidationService } from '../../validation/validation.service';

@Component({
  selector: '[fieldErrors]',
  templateUrl: './field-error.component.html',
})
export class FieldErrorComponent {
  constructor(private injector: Injector, private validationService: ValidationService) {}

  get errors() {
    const matFormField = this.injector.get(MatFormField);
    return this.validationService.getValidation(matFormField._control.ngControl?.errors || null);
  }
}
