import { Component, OnInit } from '@angular/core';
import { TimetableHearingStatement } from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../core/components/dialog/dialog.service';

@Component({
  selector: 'app-statement-detail',
  templateUrl: './statement-detail.component.html',
  styleUrls: ['./statement-detail.component.scss'],
})
export class StatementDetailComponent implements OnInit {
  statement: TimetableHearingStatement | undefined;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    protected dialogService: DialogService
  ) {}

  ngOnInit() {
    this.statement = this.activatedRoute.snapshot.data.statement;
  }
}
