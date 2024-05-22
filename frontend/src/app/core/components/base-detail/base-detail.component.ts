import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BaseDetailController} from './base-detail-controller';
import {KeepaliveService} from '../../keepalive/keepalive.service';
import {Record} from './record';
import {Subscription} from 'rxjs';
import {PermissionService} from "../../auth/permission.service";

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './base-detail.component.html',
  styleUrls: ['./base-detail.component.scss'],
})
export class BaseDetailComponent implements OnInit, OnDestroy {
  @Input() controller!: BaseDetailController<Record>;
  @Input() headingNew!: string;
  @Input() formDetailHeading!: string;
  selectedRecord!: Record;
  private recordSubscription!: Subscription;

  constructor(
    private readonly keepaliveService: KeepaliveService,
    public permissionService: PermissionService
  ) {
    keepaliveService.startWatching(() => {
      this.controller.closeConfirmDialog();
      this.controller.backToOverview();
    });
  }

  receiveWorkflowEvent() {
    this.controller.reloadRecord();
  }

  ngOnInit(): void {
    this.selectedRecord = this.controller.record;
    this.recordSubscription = this.controller.selectedRecordChange.subscribe(
      (value) => (this.selectedRecord = value)
    );
  }

  ngOnDestroy(): void {
    this.keepaliveService.stopWatching();
    this.recordSubscription.unsubscribe();
  }

  isEditButtonVisible() {
    return (
      this.selectedRecord.status !== 'IN_REVIEW' ||
      this.permissionService.isAtLeastSupervisor(this.controller.getApplicationType())
    );
  }
}
