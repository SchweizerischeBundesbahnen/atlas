import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
    selector: 'app-workflow-form',
    templateUrl: './workflow-form.component.html',
    standalone: false
})
export class WorkflowFormComponent {
  @Input() formGroup!: FormGroup;
  @Input() commentLabel!: string;
  @Input() personLabel!: string;
  @Input() hasMail = true;
}
