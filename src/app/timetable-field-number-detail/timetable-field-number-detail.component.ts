import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, Version } from '../api';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
})
export class TimetableFieldNumberDetailComponent {
  id: number;
  version: Version = {};
  editable = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private timetableFieldNumberService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.id = parseInt(this.activatedRoute.snapshot.paramMap.get('id')!);
    this.getVersion();

    if (!this.id) {
      this.enableEdit();
    }
  }

  getVersion(): void {
    if (this.id) {
      this.timetableFieldNumberService
        .getVersion(this.id)
        .subscribe((version) => (this.version = version));
    }
  }

  onSubmit() {
    this.editable = false;
    if (this.id) {
      this.timetableFieldNumberService.updateVersion(this.id, this.version).subscribe();
    } else {
      this.timetableFieldNumberService.createVersion(this.version).subscribe();
    }
    this.router
      .navigate([''], {
        relativeTo: this.route,
      })
      .then();
  }

  enableEdit() {
    this.editable = true;
  }

  cancel() {
    this.router
      .navigate([''], {
        relativeTo: this.route,
      })
      .then();
  }
}
