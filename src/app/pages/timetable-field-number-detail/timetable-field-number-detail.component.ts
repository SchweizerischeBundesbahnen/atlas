import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../../api';
import { DetailWrapperController } from '../../core/components/detail-wrapper/detail-wrapper-controller';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent
  extends DetailWrapperController<Version>
  implements OnInit
{
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    private formBuilder: FormBuilder
  ) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecord(): Version {
    return this.activatedRoute.snapshot.data.timetableFieldNumberDetail;
  }

  getTitle(record: Version): string | undefined {
    return record.swissTimetableFieldNumber;
  }

  updateRecord(): void {
    this.timetableFieldNumberService.updateVersion(this.getId(), this.form.value).subscribe();
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .subscribe((version) => this.router.navigate([version.id]).then(() => this.ngOnInit()));
  }

  deleteRecord(): void {
    this.timetableFieldNumberService.deleteVersion(this.getId()).subscribe();
    this.router.navigate(['']).then();
  }

  getFormGroup(version: Version): FormGroup {
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
