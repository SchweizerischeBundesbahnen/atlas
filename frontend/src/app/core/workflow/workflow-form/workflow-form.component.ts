import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { NgIf } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-workflow-form',
    templateUrl: './workflow-form.component.html',
    imports: [ReactiveFormsModule, CommentComponent, TextFieldComponent, NgIf, TranslatePipe]
})
export class WorkflowFormComponent {
  @Input() formGroup!: FormGroup;
  @Input() commentLabel!: string;
  @Input() personLabel!: string;
  @Input() hasMail = true;
}
