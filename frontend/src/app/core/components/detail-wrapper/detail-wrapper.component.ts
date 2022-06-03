import { Component, Input, OnDestroy } from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';
import { AuthService } from '../../auth/auth.service';
import { KeepaliveService } from '../../keepalive/keepalive.service';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent<TYPE> implements OnDestroy {
  @Input() controller!: DetailWrapperController<TYPE>;
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

  get hasAdminRole(): boolean {
    return this.authService.hasAnyRole(this.controller.getRolesAllowedToDelete());
  }

  get hasAdminOrWriterRole(): boolean {
    return this.authService.hasAnyRole(this.controller.getRolesAllowedToEdit());
  }

  ngOnDestroy(): void {
    this.keepaliveService.stopWatching();
  }
}
