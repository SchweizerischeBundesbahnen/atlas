import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Version } from '../api';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberDetailFormService {
  constructor(private formBuilder: FormBuilder) {}

  buildVersionForm(version: Version): FormGroup {
    return this.formBuilder.group({
      businessOrganisation: [
        version.businessOrganisation,
        [Validators.required, Validators.maxLength(255)],
      ],
    });
  }
}
