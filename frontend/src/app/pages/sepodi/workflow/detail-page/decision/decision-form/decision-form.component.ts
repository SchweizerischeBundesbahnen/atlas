import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DecisionFormGroup } from './decision-form-group';
import { JudgementType } from 'src/app/api';
import { MatChip, MatChipListbox } from '@angular/material/chips';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'decision-form',
  templateUrl: './decision-form.component.html',
  styleUrls: ['decision-form.component.scss'],
  imports: [
    ReactiveFormsModule,
    MatChipListbox,
    MatChip,
    TextFieldComponent,
    MatRadioGroup,
    MatRadioButton,
    AtlasFieldErrorComponent,
    CommentComponent,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class DecisionFormComponent {
  protected readonly JudgementType = JudgementType;

  @Input() form!: FormGroup<DecisionFormGroup>;
  @Input() hasOverride = false;
  @Input() hasDecisionTypeVotedExpired = false;
  @Input() showJudgement = true;

  get warningChipMessage(): string | undefined {
    if (this.hasOverride) {
      return 'SEPODI.SERVICE_POINTS.WORKFLOW.OVERRIDE_HAPPENED_INFO';
    }
    if (this.hasDecisionTypeVotedExpired) {
      return 'SEPODI.SERVICE_POINTS.WORKFLOW.VOTED_EXPIRATION';
    }
    return undefined;
  }
}
