import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../api';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent implements OnInit {
  id: number;
  version!: Version;

  constructor(
    private activatedRoute: ActivatedRoute,
    private timetableFieldNumberService: TimetableFieldNumbersService
  ) {
    this.id = parseInt(this.activatedRoute.snapshot.paramMap.get('id')!);
    this.getVersion();
  }

  ngOnInit(): void {}

  getVersion(): void {
    this.timetableFieldNumberService
      .getVersion(this.id)
      .subscribe((version) => (this.version = version));
  }
}
