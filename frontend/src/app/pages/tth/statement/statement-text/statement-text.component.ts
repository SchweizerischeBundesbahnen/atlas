import { Component, input, OnInit } from '@angular/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { StatementDetailFormGroup } from '../statement-detail/statement-detail-form-group';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';

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
  templateUrl: './statement-text.component.html',
  providers: [TranslatePipe],
})
export class StatementTextComponent implements OnInit {
  form = input.required<FormGroup<StatementDetailFormGroup>>();
  isNew = input.required<boolean>();

  showOriginalText = false;
  hasAnonymousText = false;

  ngOnInit(): void {
    this.initForm(this.form());
  }

  resetForm(formGroup: FormGroup<StatementDetailFormGroup>) {
    this.initForm(formGroup);
  }

  initForm(formGroup: FormGroup<StatementDetailFormGroup>) {
    this.hasAnonymousText = !!formGroup.controls.anonymousStatement.value;
    this.showOriginalText = !formGroup.controls.anonymousStatement.value;
  }

  toggleOriginalText() {
    this.showOriginalText = !this.showOriginalText;
  }
}
