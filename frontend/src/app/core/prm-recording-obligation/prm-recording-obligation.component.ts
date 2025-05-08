import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { ApplicationType } from '../../api';
import { PermissionService } from '../auth/permission/permission.service';
import { PersonWithReducedMobilityService } from '../../api/service/prm/person-with-reduced-mobility.service';
import { NotificationService } from '../notification/notification.service';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSlideToggleComponent } from '../form-components/atlas-slide-toggle/atlas-slide-toggle.component';

@Component({
  selector: 'prm-recording-obligation',
  templateUrl: './prm-recording-obligation.component.html',
  imports: [TranslatePipe, AtlasSlideToggleComponent],
})
export class PrmRecordingObligationComponent implements OnInit, OnChanges {
  recordingObligation = true;
  isPrmSupervisor = false;

  @Input() sloid!: string;
  @Input() showToggle = true;

  constructor(
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private permissionService: PermissionService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.isPrmSupervisor = this.permissionService.isAtLeastSupervisor(
      ApplicationType.Prm
    );

    this.initCurrentRecordingObligation();
  }

  ngOnChanges() {
    this.initCurrentRecordingObligation();
  }

  private initCurrentRecordingObligation() {
    this.personWithReducedMobilityService
      .getRecordingObligation(this.sloid)
      .subscribe(
        (recordingObligation) =>
          (this.recordingObligation = recordingObligation.value)
      );
  }

  toggleRecordingObligation() {
    this.personWithReducedMobilityService
      .updateRecordingObligation(this.sloid, {
        value: !this.recordingObligation,
      })
      .subscribe(() => {
        this.notificationService.success(
          'PRM.STOP_POINTS.RECORDING_OBLIGATION_SAVED'
        );
        this.recordingObligation = !this.recordingObligation;
      });
  }
}
