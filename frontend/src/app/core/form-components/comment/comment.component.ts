import {Component, Input} from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { MatInput } from '@angular/material/input';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';

@Component({
    selector: 'form-comment',
    templateUrl: './comment.component.html',
    styleUrls: ['./comment.component.scss'],
    imports: [ReactiveFormsModule, NgIf, AtlasLabelFieldComponent, MatInput, AtlasFieldErrorComponent]
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
