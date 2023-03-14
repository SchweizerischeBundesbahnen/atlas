import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { LineType, PaymentType } from '../../../../../api';

@Component({
  selector: 'line-detail-form',
  templateUrl: './line-detail-form.component.html',
  styleUrls: ['./line-detail-form.component.scss'],
})
export class LineDetailFormComponent {
  @Input() form!: FormGroup;
  @Input() newRecord = false;
  @Input() boSboidRestriction: string[] = [];
  TYPE_OPTIONS = Object.values(LineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);
}
