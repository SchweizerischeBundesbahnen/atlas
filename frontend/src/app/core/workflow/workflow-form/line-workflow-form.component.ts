import { Component, Input } from '@angular/core';
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
  @Input() formGroup!: FormGroup;
  @Input() commentLabel!: string;
  @Input() personLabel!: string;
  @Input() hasMail = true;
}
