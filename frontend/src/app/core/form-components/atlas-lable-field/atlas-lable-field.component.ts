import { Component, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { FieldExample } from '../text-field/field-example';

@Component({
  selector: 'app-atlas-lable-field',
  templateUrl: './atlas-lable-field.component.html',
  styleUrls: ['./atlas-lable-field.component.scss'],
})
export class AtlasLableFieldComponent {
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
