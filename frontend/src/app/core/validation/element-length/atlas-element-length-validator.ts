import {AbstractControl, ValidationErrors} from "@angular/forms";

export class AtlasElementLengthValidator {

  static maxElements(control: AbstractControl): ValidationErrors | null {
    const value: string = control.value;
    const elements: string[] = value.split(/[;,]/).map(elem => elem.trim());
    const uniqueElements = [...new Set(elements)];
    return uniqueElements.length > 10 ? { maxElements: true } : null;
  }

  static noDuplicates(control: AbstractControl): ValidationErrors | null {
    const value: string = control.value;
    const elements: string[] = value.split(/[;,]/).map(elem => elem.trim());
    const uniqueElements = [...new Set(elements)];
    return uniqueElements.length !== elements.length ? { noDuplicates: true } : null;
  }

}
