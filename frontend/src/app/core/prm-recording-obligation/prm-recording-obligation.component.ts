import { Component, inject, input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ApplicationType } from '../../api';
import { PermissionService } from '../auth/permission/permission.service';
import { StopPointInternalService } from '../../api/service/prm/stop-point/stop-point-internal.service';
import { NotificationService } from '../notification/notification.service';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSlideToggleComponent } from '../form-components/atlas-slide-toggle/atlas-slide-toggle.component';

@Component({
  selector: 'atlas-prm-recording-obligation',
  templateUrl: './prm-recording-obligation.component.html',
  imports: [TranslatePipe, AtlasSlideToggleComponent],
  providers: [TranslatePipe],
})
export class PrmRecordingObligationComponent implements OnInit, OnChanges {
  recordingObligation = true;
  isPrmSupervisor = false;

  readonly sloid = input.required<string>();
  readonly showToggle = input(true);

  private readonly stopPointInternalService = inject(StopPointInternalService);
  private readonly permissionService = inject(PermissionService);
  private readonly notificationService = inject(NotificationService);

  ngOnInit(): void {
    this.isPrmSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Prm);

    this.initCurrentRecordingObligation();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.sloid) {
      this.initCurrentRecordingObligation();
    }
  }

  private initCurrentRecordingObligation() {
    this.stopPointInternalService
      .getRecordingObligation(this.sloid())
      .subscribe((recordingObligation) => (this.recordingObligation = recordingObligation.value));
  }

  toggleRecordingObligation() {
    this.stopPointInternalService
      .updateRecordingObligation(this.sloid(), {
        value: !this.recordingObligation,
      })
      .subscribe(() => {
        this.notificationService.success('PRM.STOP_POINTS.RECORDING_OBLIGATION_SAVED');
        this.recordingObligation = !this.recordingObligation;
      });
  }
}
