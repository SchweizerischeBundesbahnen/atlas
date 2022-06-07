import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'form-comment',
  templateUrl: './comment.component.html',
})
export class CommentComponent {
  @Input() formGroup!: UntypedFormGroup;
}
