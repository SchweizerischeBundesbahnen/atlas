import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'form-comment',
  templateUrl: './comment.component.html',
})
export class CommentComponent {
  @Input() formGroup!: FormGroup;
  @Input() displayInfoIcon = true;
  @Input() displayLabel = true;
  @Input() label = 'FORM.COMMENT';
}
