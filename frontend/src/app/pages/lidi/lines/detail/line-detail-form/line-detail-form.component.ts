import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { LineType, PaymentType } from '../../../../../api';

@Component({
  selector: 'line-deatil-form',
  templateUrl: './line-detail-form.component.html',
})
export class LineDetailFormComponent {
  @Input() form!: FormGroup;
  @Input() newRecord = false;
  @Input() boSboidRestriction: string[] = [];
  TYPE_OPTIONS = Object.values(LineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);
}
