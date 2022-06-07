import { Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'form-url-component',
  templateUrl: './url.component.html',
  styleUrls: ['url.component.scss'],
})
export class UrlComponent {
  @Input() formGroup!: UntypedFormGroup;
}
