import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../api';
import { DetailWrapperController } from '../core/components/detail-wrapper/detail-wrapper-controller';
import { TimetableFieldNumberDetailFormService } from './timetable-field-number-detail-form.service';
import { Observable } from 'rxjs';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent extends DetailWrapperController<Version> {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    public timetableFieldNumberDetailFormService: TimetableFieldNumberDetailFormService
  ) {
    super(activatedRoute);
  }

  readRecord(): Observable<Version> {
    return this.timetableFieldNumberService.getVersion(this.getId());
  }

  getFormGroup(record: Version): FormGroup {
    return this.timetableFieldNumberDetailFormService.buildVersionForm(record);
  }

  getTitle(record: Version): string | undefined {
    return record.swissTimetableFieldNumber;
  }

  updateRecord(): void {
    this.timetableFieldNumberService.updateVersion(this.getId(), this.record).subscribe();
  }

  createRecord(): void {
    this.timetableFieldNumberService.createVersion(this.record).subscribe();
  }

  deleteRecord(): void {
    this.timetableFieldNumberService.deleteVersion(this.getId()).subscribe();
    this.router.navigate(['']).then();
  }
}
