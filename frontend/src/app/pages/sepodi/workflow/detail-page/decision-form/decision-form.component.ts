import {Component, Input} from '@angular/core';
import {ControlContainer, FormGroup, FormsModule, NgForm, ReactiveFormsModule,} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {TranslateModule} from '@ngx-translate/core';
import {MatDialogClose} from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import {FormModule} from '../../../../../core/module/form.module';
import {CoreModule} from '../../../../../core/module/core.module';
import {DecisionFormGroup} from "./decision-form-group";
import {JudgementType} from "../../../../../api";

@Component({
  selector: 'decision-form',
  standalone: true,
  imports: [
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    MatDialogClose,
    MatIconModule,
    FormModule,
    CoreModule,
  ],
  templateUrl: './decision-form.component.html',
  styleUrl: './decision-form.component.scss',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class DecisionFormComponent {

  protected readonly JudgementType = JudgementType;

  @Input() formGroup!: FormGroup<DecisionFormGroup>;
}
