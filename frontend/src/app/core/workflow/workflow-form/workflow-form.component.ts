import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-workflow-form',
  templateUrl: './workflow-form.component.html',
})
export class WorkflowFormComponent {
  @Input() formGroup!: FormGroup;
}
