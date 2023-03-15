import { Component, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldExample } from '../text-field/field-example';

@Component({
  selector: 'app-atlas-label-field',
  templateUrl: './atlas-label-field.component.html',
})
export class AtlasLabelFieldComponent {
  @Input() required!: boolean;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() fieldExamples!: Array<FieldExample>;

  constructor(private readonly translatePipe: TranslatePipe) {}

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
