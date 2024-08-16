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
  @Input() hasDecisionTypeVotedExpired = false;
  @Input() showJudgement = true;

  get warningChipMessage(): string | undefined {
    if (this.hasOverride) {
      return 'SEPODI.SERVICE_POINTS.WORKFLOW.OVERRIDE_HAPPENED_INFO'
    }
    if (this.hasDecisionTypeVotedExpired) {
      return 'SEPODI.SERVICE_POINTS.WORKFLOW.VOTED_EXPIRATION'
    }
    return undefined;
  }
}
