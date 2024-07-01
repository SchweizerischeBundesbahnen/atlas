import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {DecisionFormGroup} from './decision-form-group';
import {JudgementType} from 'src/app/api';

@Component({
  selector: 'decision-form',
  templateUrl: './decision-form.component.html',
  styleUrls: ['decision-form.component.scss'],
})
export class DecisionFormComponent {
  protected readonly JudgementType = JudgementType;

  @Input() form!: FormGroup<DecisionFormGroup>;
  @Input() hasOverride = false;
}
