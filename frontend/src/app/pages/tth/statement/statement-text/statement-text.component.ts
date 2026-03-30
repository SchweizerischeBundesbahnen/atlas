import {
  ChangeDetectionStrategy,
  Component,
  input,
  OnInit,
} from '@angular/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { StatementDetailFormGroup } from '../statement-detail/statement-detail-form-group';
import { StatementText } from './stetement-text';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';

export const HIDE_ORIGINAL_TEXT_LABEL = 'TTH.STATEMENT.HIDE_ORIGINAL_TEXT';
export const SHOW_ORIGINAL_TEXT_LABEL = 'TTH.STATEMENT.SHOW_ORIGINAL_TEXT';
export const UNLOCK_ICON = 'bi-lock';
export const LOCK_ICON = 'bi-lock-fill';

@Component({
  selector: 'atlas-statement-text',
  imports: [
    AtlasButtonComponent,
    AtlasLabelFieldComponent,
    CommentComponent,
    ReactiveFormsModule,
    TranslatePipe,
    AtlasSpacerComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './statement-text.component.html',
  providers: [TranslatePipe],
})
export class StatementTextComponent implements OnInit {
  form = input.required<FormGroup<StatementDetailFormGroup>>();
  isNew = input.required<boolean>();
  statementText = new StatementText();

  ngOnInit(): void {
    this.initForm(this.form());
  }

  resetForm(formGroup: FormGroup<StatementDetailFormGroup>) {
    this.initForm(formGroup);
  }

  initForm(formGroup: FormGroup<StatementDetailFormGroup>) {
    this.statementText.checkboxStatementAnonymous =
      formGroup.getRawValue().statementAnonymous!;
    if (formGroup.getRawValue().anonymousStatement) {
      this.statementText.showOriginalStatementText = false;
      this.statementText.showOriginalTextButton = true;
    } else {
      this.statementText.showOriginalStatementText = true;
      this.statementText.showOriginalTextButton = false;
    }
  }

  checkStatementAnonymous(checked: boolean) {
    this.statementText.checkboxStatementAnonymous = checked;
    this.statementText.showOriginalStatementText = true;
    this.statementText.showOriginalTextButton = false;
    if (checked) {
      this.statementText.currentAnonymousStatement =
        this.form().getRawValue().anonymousStatement!;
      this.form().controls.anonymousStatement.setValue(null);
    } else if (this.statementText.currentAnonymousStatement) {
      this.form().controls.anonymousStatement.setValue(
        this.statementText.currentAnonymousStatement
      );
    }
  }

  showOriginalText() {
    this.statementText.showOriginalStatementText =
      !this.statementText.showOriginalStatementText;
    if (this.statementText.showOriginalStatementText) {
      this.statementText.originalTextButtonLabel = HIDE_ORIGINAL_TEXT_LABEL;
      this.statementText.originalTextButtonIcon = UNLOCK_ICON;
    } else {
      this.statementText.originalTextButtonLabel = SHOW_ORIGINAL_TEXT_LABEL;
      this.statementText.originalTextButtonIcon = LOCK_ICON;
    }
  }
}
