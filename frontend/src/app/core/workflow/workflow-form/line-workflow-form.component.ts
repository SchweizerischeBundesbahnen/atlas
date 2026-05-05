import { Component, Input, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-workflow-form',
  templateUrl: './line-workflow-form.component.html',
  imports: [ReactiveFormsModule, CommentComponent, TextFieldComponent, TranslatePipe],
  providers: [TranslatePipe],
})
export class LineWorkflowFormComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly commentLabel = input.required<string>();
  readonly personLabel = input.required<string>();
  readonly hasMail = input(true);
}
