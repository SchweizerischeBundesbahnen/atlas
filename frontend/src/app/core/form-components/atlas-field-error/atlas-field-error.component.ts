import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { ValidationService } from '../../validation/validation.service';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EMPTY, merge, Observable, of, startWith } from 'rxjs';
import { ValidationError } from '../../validation/validation-error';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'atlas-field-error',
  templateUrl: './atlas-field-error.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./atlas-field-error.component.scss'],
  imports: [TranslatePipe, AsyncPipe],
})
export class AtlasFieldErrorComponent {
  readonly control = input.required<AbstractControl | null>();

  protected errors$: Observable<ValidationError[]> = of();

  constructor() {
    effect(() => {
      const ctrl = this.control();
      if (!ctrl) {
        this.errors$ = of([]);
        return;
      }
      this.errors$ = merge(ctrl.valueChanges, ctrl.parent?.statusChanges ?? EMPTY).pipe(
        startWith(this.getErrors(ctrl.errors)),
        map(() => this.getErrors(ctrl.errors))
      );
    });
  }

  private readonly validationService = inject(ValidationService);

  private getErrors(ctrlErrors?: ValidationErrors | null) {
    return this.validationService.getValidation(ctrlErrors ?? null);
  }
}
