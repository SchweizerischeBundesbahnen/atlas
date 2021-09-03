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
      swissTimetableFieldNumber: [
        version.swissTimetableFieldNumber,
        [Validators.required, Validators.maxLength(255)],
      ],
      ttfnid: [version.ttfnid, [Validators.required, Validators.maxLength(255)]],
      validFrom: [version.validFrom, [Validators.required, Validators.maxLength(255)]],
      validTo: [version.validTo, [Validators.required, Validators.maxLength(255)]],
      businessOrganisation: [
        version.businessOrganisation,
        [Validators.required, Validators.maxLength(255)],
      ],
      number: [version.number, [Validators.required, Validators.maxLength(255)]],
      name: [version.name, [Validators.required, Validators.maxLength(255)]],
      comment: [version.comment],
    });
  }
}
