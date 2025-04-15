import { Component, Input, OnInit } from '@angular/core';
import { ApplicationType } from '../../api';
import { PermissionService } from '../auth/permission/permission.service';
import {PersonWithReducedMobilityService} from "../../api/service/personWithReducedMobility.service";

@Component({
  selector: 'prm-recording-obligation',
  templateUrl: './prm-recording-obligation.component.html',
})
export class PrmRecordingObligationComponent implements OnInit {
  recordingObligation = true;
  isPrmSupervisor = false;

  @Input() sloid!: string;
  @Input() showToggle = true;

  constructor(
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.isPrmSupervisor = this.permissionService.isAtLeastSupervisor(
      ApplicationType.Prm
    );

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
      .subscribe(() => (this.recordingObligation = !this.recordingObligation));
  }
}
