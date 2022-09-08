import { Component, Input, OnDestroy } from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';
import { AuthService } from '../../auth/auth.service';
import { KeepaliveService } from '../../keepalive/keepalive.service';
import { Record } from './record';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent implements OnDestroy {
  @Input() controller!: DetailWrapperController<Record>;
  @Input() headingNew!: string;
  @Input() formDetailHeading!: string;

  constructor(
    private readonly authService: AuthService,
    private readonly keepaliveService: KeepaliveService
  ) {
    keepaliveService.startWatching(() => {
      this.controller.closeConfirmDialog();
      this.controller.backToOverview();
    });
  }

  get mayDelete(): boolean {
    return this.authService.isAdmin;
  }

  get mayWrite(): boolean {
    return this.authService.hasPermissionsToWrite(
      this.controller.getApplicationType(),
      this.controller.record.businessOrganisation
    );
  }

  ngOnDestroy(): void {
    this.keepaliveService.stopWatching();
  }
}
