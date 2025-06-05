import { Component, inject, input, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { DateService } from '../../../../../../core/date/date.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-stop-point-termination-info',
  imports: [TranslatePipe],
  styleUrls: ['./stop-point-termination-info.component.scss'],
  templateUrl: './stop-point-termination-info.component.html',
})
export class StopPointTerminationInfoComponent implements OnInit {
  private readonly workflowService = inject(WorkflowService);
  private readonly router = inject(Router);
  readonly sloid = input.required<string>();

  private _terminationDate!: string;
  private _workflowId!: number;

  get terminationDate() {
    return this._terminationDate;
  }

  set terminationDate(terminationDate: string) {
    this._terminationDate = terminationDate;
  }

  get workflowId() {
    return this._workflowId;
  }

  set workflowId(value: number) {
    this._workflowId = value;
  }

  ngOnInit(): void {
    this.workflowService
      .getTerminationInfoBySloid(this.sloid())
      .subscribe((terminationInfo) => {
        this.terminationDate = DateService.getDateFormatted(
          terminationInfo.terminationDate
        );
        if (terminationInfo.workflowId != null) {
          this.workflowId = terminationInfo.workflowId;
        }
      });
  }

  navigate() {
    this.router
      .navigateByUrl(
        `/service-point-directory/termination-workflows/${this.terminationDate}`
      )
      .then();
  }
}
