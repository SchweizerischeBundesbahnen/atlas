import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../api';
import { DetailWrapperController } from '../core/components/detail-wrapper/detail-wrapper-controller';
import { TimetableFieldNumberDetailFormService } from './timetable-field-number-detail-form.service';
import { FormGroup } from '@angular/forms';

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
    public activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    public timetableFieldNumberDetailFormService: TimetableFieldNumberDetailFormService
  ) {
    super(activatedRoute);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecord(): Version {
    return this.activatedRoute.snapshot.data.timetableFieldNumberDetail;
  }

  getFormGroup(record: Version): FormGroup {
    return this.timetableFieldNumberDetailFormService.buildVersionForm(record);
  }

  getTitle(record: Version): string | undefined {
    return record.swissTimetableFieldNumber;
  }

  updateRecord(): void {
    this.timetableFieldNumberService.updateVersion(this.getId(), this.form.value).subscribe();
  }

  createRecord(): void {
    this.timetableFieldNumberService.createVersion(this.form.value).subscribe();
  }

  deleteRecord(): void {
    this.timetableFieldNumberService.deleteVersion(this.getId()).subscribe();
    this.router.navigate(['']).then();
  }
}
