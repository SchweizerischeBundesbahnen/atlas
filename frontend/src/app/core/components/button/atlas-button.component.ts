import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ApplicationRole, ApplicationType } from '../../../api';
import { AuthService } from '../../auth/auth.service';
import { AtlasButtonType } from './atlas-button.type';
import { NON_PROD_STAGES } from '../../constants/stages';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'atlas-button[buttonType]',
  templateUrl: './atlas-button.component.html',
  styleUrls: ['./atlas-button.component.scss'],
})
export class AtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() wrapperStyleClass!: string;
  @Input() buttonDataCy!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() submitButton!: boolean;
  @Input() disabled!: boolean;
  @Input() buttonText!: string;

  @Output() buttonClicked = new EventEmitter<void>();

  constructor(private authService: AuthService) {}

  isButtonVisible() {
    if (this.buttonType === AtlasButtonType.CREATE_CHECKING_PERMISSION) {
      return this.mayCreate();
    }
    if (this.buttonType === AtlasButtonType.EDIT) {
      return this.mayEdit();
    }
    if (this.buttonType === AtlasButtonType.REVOKE) {
      return this.mayRevoke();
    }
    if (this.buttonType === AtlasButtonType.DELETE) {
      return this.mayDelete();
    }
    return true;
  }

  mayCreate() {
    if (!this.applicationType) {
      throw new Error('Permission checking button needs applicationtype');
    }
    return this.authService.hasPermissionsToCreate(this.applicationType);
  }

  mayEdit() {
    if (!this.applicationType) {
      throw new Error('Edit button needs applicationtype');
    }
    if (this.applicationType !== ApplicationType.Bodi && !this.businessOrganisation) {
      throw new Error('Edit button needs businessOrganisation');
    }
    return this.authService.hasPermissionsToWrite(this.applicationType, this.businessOrganisation);
  }

  mayRevoke(): boolean {
    if (!this.applicationType) {
      throw new Error('Revoke button needs applicationtype');
    }
    const applicationUserPermission = this.authService.getApplicationUserPermission(
      this.applicationType
    );
    return (
      this.authService.isAdmin || applicationUserPermission.role === ApplicationRole.Supervisor
    );
  }

  mayDelete(): boolean {
    return this.authService.isAdmin && NON_PROD_STAGES.includes(environment.label);
  }

  getButtonStyleClass() {
    if (
      [AtlasButtonType.CREATE, AtlasButtonType.CREATE_CHECKING_PERMISSION].includes(this.buttonType)
    ) {
      return 'mat-raised-button';
    }
    if (this.buttonType === AtlasButtonType.FOOTER) {
      return 'atlas-primary-btn footer-btn';
    }
    return 'atlas-primary-btn';
  }
}
