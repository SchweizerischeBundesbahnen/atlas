import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ApplicationType } from '../../../api';
import { AuthService } from '../../auth/auth.service';
import { AtlasButtonType } from './atlas-button.type';

@Component({
  selector: 'atlas-button[buttonType]',
  templateUrl: './atlas-button.component.html',
  styleUrls: ['./atlas-button.component.scss'],
})
export class AtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() submitButton!: boolean;
  @Input() disabled!: boolean;
  @Input() buttonText!: string;

  readonly RAISED_BUTTONS = [AtlasButtonType.CREATE, AtlasButtonType.CREATE_CHECKING_PERMISSION];

  @Output() buttonClicked = new EventEmitter<void>();

  constructor(private authService: AuthService) {}

  isButtonVisible() {
    if (this.buttonType === AtlasButtonType.CREATE_CHECKING_PERMISSION) {
      if (!this.applicationType) {
        throw new Error('Permission checking button needs applicationtype');
      }
      return this.authService.hasPermissionsToCreate(this.applicationType);
    }
    if (this.buttonType === AtlasButtonType.EDIT) {
      if (!this.applicationType || !this.businessOrganisation) {
        throw new Error(
          'Permission checking button needs applicationtype and businessOrganisation'
        );
      }
      return this.authService.hasPermissionsToWrite(
        this.applicationType,
        this.businessOrganisation
      );
    }
    return true;
  }

  getButtonStyleClass() {
    if (this.RAISED_BUTTONS.includes(this.buttonType)) {
      return 'mat-raised-button';
    }
    if (this.buttonType === AtlasButtonType.FOOTER) {
      return 'atlas-primary-btn footer-btn';
    }
    if (this.buttonType === AtlasButtonType.EDIT) {
      return 'atlas-primary-btn';
    }
    return '';
  }
}
