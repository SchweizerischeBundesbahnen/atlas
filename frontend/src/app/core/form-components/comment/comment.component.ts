import { Component, Input, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { MatInput } from '@angular/material/input';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-form-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
  imports: [ReactiveFormsModule, AtlasLabelFieldComponent, MatInput, AtlasFieldErrorComponent],
  providers: [TranslatePipe],
})
export class CommentComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly displayLabel = input(true);
  readonly required = input(false);
  readonly label = input('FORM.COMMENT');
  readonly subLabel = input('FORM.TEXT');
  readonly controlName = input('comment');
  readonly maxChars = input('1500');
  readonly info = input<string>();
  readonly readonly = input(false);
}
