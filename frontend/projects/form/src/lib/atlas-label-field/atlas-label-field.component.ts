import { Component, inject, Input, input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { NgClass } from '@angular/common';
import { FieldExample } from '../atlas-text-field/field-example';
import { AtlasInfoLinkDirective } from '../atlas-info-icon/atlas-info-link.directive';
import { AtlasInfoIconComponent } from '../atlas-info-icon/atlas-info-icon.component';

@Component({
  selector: 'atlas-label-field',
  templateUrl: './atlas-label-field.component.html',
  imports: [NgClass, AtlasInfoLinkDirective, TranslatePipe, AtlasInfoIconComponent],
  providers: [TranslatePipe],
})
export class AtlasLabelFieldComponent {
  private readonly translatePipe = inject(TranslatePipe);

  readonly required = input(false);
  readonly fieldLabel = input.required<string>();

  @Input() infoIconTitle?: string;

  @Input() infoIconLink?: string;

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
