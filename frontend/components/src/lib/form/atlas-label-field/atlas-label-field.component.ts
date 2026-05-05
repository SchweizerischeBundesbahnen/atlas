import { Component, inject, Input, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { NgClass } from '@angular/common';
import { FieldExample } from '../../../../../src/app/core/form-components/text-field/field-example';
import { InfoLinkDirective } from '../info-icon/info-link.directive';
import { InfoIconComponent } from '../info-icon/info-icon.component';

@Component({
  selector: 'atlas-label-field',
  templateUrl: './atlas-label-field.component.html',
  imports: [NgClass, InfoLinkDirective, TranslatePipe, InfoIconComponent],
  providers: [TranslatePipe],
})
export class AtlasLabelFieldComponent {
  private readonly translatePipe = inject(TranslatePipe);

  readonly required = input(false);
  readonly fieldLabel = input.required<string>();
  // TODO: Skipped for migration because:
  //  This input is used in a control flow expression (e.g. `@if` or `*ngIf`)
  //  and migrating would break narrowing currently.
  @Input() infoIconTitle?: string;
  // TODO: Skipped for migration because:
  //  This input is used in a control flow expression (e.g. `@if` or `*ngIf`)
  //  and migrating would break narrowing currently.
  @Input() infoIconLink?: string;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() fieldExamples!: Array<FieldExample>;

  translate(fieldExample: FieldExample): string {
    if (fieldExample.label && !fieldExample.arg) {
      return this.translatePipe.transform(fieldExample.label);
    }
    if (fieldExample.label && fieldExample.arg) {
      return this.translatePipe.transform(fieldExample.label, {
        [fieldExample.arg!.key]: fieldExample.arg?.value,
      });
    }
    return fieldExample.label!;
  }
}
