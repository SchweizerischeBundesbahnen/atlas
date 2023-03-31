import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'form-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
})
export class CommentComponent {
  @Input() formGroup!: FormGroup;
  @Input() displayInfoIcon = true;
  @Input() displayLabel = true;
  @Input() required = false;
  @Input() label = 'FORM.COMMENT';
  @Input() controlName = 'comment';
}
