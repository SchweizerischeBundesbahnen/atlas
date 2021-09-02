import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../api';
import { DetailWrapperController } from '../core/components/detail-wrapper/detail-wrapper-controller';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent extends DetailWrapperController {
  version: Version = {};

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private timetableFieldNumberService: TimetableFieldNumbersService
  ) {
    super(activatedRoute);
  }

  readRecord(): void {
    this.timetableFieldNumberService
      .getVersion(this.getId())
      .subscribe((version) => (this.version = version));
  }

  updateRecord(): void {
    this.timetableFieldNumberService.updateVersion(this.getId(), this.version).subscribe();
  }

  createRecord(): void {
    this.timetableFieldNumberService.createVersion(this.version).subscribe();
  }

  deleteRecord(): void {
    this.timetableFieldNumberService.deleteVersion(this.getId()).subscribe();
    this.router.navigate(['']).then();
  }
}
