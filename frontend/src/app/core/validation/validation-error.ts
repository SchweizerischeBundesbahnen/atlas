import { ValidationErrors } from '@angular/forms';

export interface ValidationError {
  error: string;
  value: ValidationErrors;
  params?: TranslationParameter;
}

export interface TranslationParameter {
  date?: string;
  length?: string;
  allowedChars?: string;
  maxDecimalDigits?: string;
  numbersWithColons?: string;
  max?: string;
  min?: string;
  number?: string;
}
