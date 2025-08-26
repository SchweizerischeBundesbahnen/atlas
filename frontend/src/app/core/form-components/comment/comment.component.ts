import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { MatInput } from '@angular/material/input';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'form-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss'],
  imports: [
    ReactiveFormsModule,
    NgIf,
    AtlasLabelFieldComponent,
    MatInput,
    AtlasFieldErrorComponent,
  ],
  providers: [TranslatePipe],
})
export class CommentComponent {
  @Input() formGroup!: FormGroup;
  @Input() displayLabel = true;
  @Input() required = false;
  @Input() label = 'FORM.COMMENT';
  @Input() subLabel = 'FORM.TEXT';
  @Input() controlName = 'comment';
  @Input() maxChars = '1500';
  @Input() info!: string;
}
