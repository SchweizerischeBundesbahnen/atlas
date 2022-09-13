import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { BaseDetailController } from './base-detail-controller';
import { AuthService } from '../../auth/auth.service';
import { KeepaliveService } from '../../keepalive/keepalive.service';
import { Record } from './record';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './base-detail.component.html',
  styleUrls: ['./base-detail.component.scss'],
})
export class BaseDetailComponent implements OnInit, OnDestroy {
  @Input() controller!: BaseDetailController<Record>;
  @Input() headingNew!: string;
  @Input() formDetailHeading!: string;

  mayWrite = false;
  private recordSubscription!: Subscription;

  constructor(
    private readonly authService: AuthService,
    private readonly keepaliveService: KeepaliveService
  ) {
    keepaliveService.startWatching(() => {
      this.controller.closeConfirmDialog();
      this.controller.backToOverview();
    });
  }

  ngOnInit(): void {
    this.evaluateWritePermissions();
    this.recordSubscription = this.controller.selectedRecordChange.subscribe(() =>
      this.evaluateWritePermissions()
    );
  }

  evaluateWritePermissions() {
    this.mayWrite = this.authService.hasPermissionsToWrite(
      this.controller.getApplicationType(),
      this.controller.record.businessOrganisation
    );
  }

  get mayDelete(): boolean {
    return this.authService.isAdmin;
  }

  ngOnDestroy(): void {
    this.keepaliveService.stopWatching();
    this.recordSubscription.unsubscribe();
  }
}
