import { Component, Input } from '@angular/core';
import { DetailWrapperController } from './detail-wrapper-controller';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  templateUrl: './detail-wrapper.component.html',
  styleUrls: ['./detail-wrapper.component.scss'],
})
export class DetailWrapperComponent<TYPE> {
  @Input() controller!: DetailWrapperController<TYPE>;
  @Input() headingNew!: string;
  @Input() formDetailHeading!: string;

  constructor(private readonly authService: AuthService) {}

  get hasAdminRole(): boolean {
    return this.authService.hasAnyRole(this.controller.getRolesAllowedToDelete());
  }

  get hasAdminOrWriterRole(): boolean {
    return this.authService.hasAnyRole(this.controller.getRolesAllowedToEdit());
  }
}
