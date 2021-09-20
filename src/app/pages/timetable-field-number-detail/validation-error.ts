import { ValidationErrors } from '@angular/forms';

export interface ValidationError {
  error: string;
  value: ValidationErrors;
}
