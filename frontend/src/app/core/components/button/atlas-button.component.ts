import { Component, ContentChild, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { ApplicationRole, ApplicationType } from '../../../api';
import { AuthService } from '../../auth/auth.service';
import { AtlasButtonType } from './atlas-button.type';
import { NON_PROD_STAGES } from '../../constants/stages';
import { environment } from '../../../../environments/environment';
import { Cantons } from '../../../pages/tth/overview/canton/Cantons';

@Component({
  selector: 'atlas-button[buttonType]',
  templateUrl: './atlas-button.component.html',
})
export class AtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() canton!: string;
  @Input() disabled!: boolean;

  @Input() wrapperStyleClass!: string;
  @Input() buttonDataCy!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() footerEdit = false;
  @Input() submitButton!: boolean;
  @Input() buttonText!: string;

  @Output() buttonClicked = new EventEmitter<void>();
  @ContentChild('rightIcon') rightIcon!: TemplateRef<any>;

  constructor(private authService: AuthService) {}

  isButtonVisible() {
    if (this.buttonType === AtlasButtonType.CREATE_CHECKING_PERMISSION) {
      return this.mayCreate();
    }
    if (this.buttonType === AtlasButtonType.EDIT) {
      return this.mayEdit();
    }
    if (
      this.buttonType === AtlasButtonType.REVOKE ||
      this.buttonType === AtlasButtonType.SKIP_WORKFLOW
    ) {
      return this.isAtLeastSupervisor();
    }
    if (this.buttonType === AtlasButtonType.DELETE) {
      return this.mayDelete();
    }
    if (this.buttonType === AtlasButtonType.CANTON_CSV_DOWNLOAD) {
      return this.mayDownloadCantonCsv();
    }
    if (
      [AtlasButtonType.FOOTER_NON_EDIT, AtlasButtonType.WHITE_FOOTER_NON_EDIT].includes(
        this.buttonType
      )
    ) {
      return !this.footerEdit;
    }
    if (
      [AtlasButtonType.FOOTER_EDIT_MODE, AtlasButtonType.WHITE_FOOTER_EDIT_MODE].includes(
        this.buttonType
      )
    ) {
      return this.footerEdit;
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

  isAtLeastSupervisor(): boolean {
    if (!this.applicationType) {
      throw new Error('Revoke button needs applicationtype');
    }
    return this.authService.isAtLeastSupervisor(this.applicationType);
  }

  mayDelete(): boolean {
    return this.authService.isAdmin && NON_PROD_STAGES.includes(environment.label);
  }

  mayDownloadCantonCsv() {
    const applicationUserPermission = this.authService.getApplicationUserPermission(
      ApplicationType.TimetableHearing
    );
    if (this.authService.isAdmin || applicationUserPermission.role === ApplicationRole.Supervisor) {
      return true;
    }
    if (applicationUserPermission.role === ApplicationRole.Writer) {
      const allowedSwissCantons = applicationUserPermission.permissionRestrictions.map(
        (restriction) => restriction.valueAsString
      );
      return allowedSwissCantons.includes(Cantons.getSwissCantonEnum(this.canton));
    }
    return false;
  }

  getButtonStyleClass() {
    if (this.buttonType === AtlasButtonType.DEFAULT_PRIMARY) {
      return 'atlas-primary-btn';
    }
    if (
      [
        AtlasButtonType.CREATE,
        AtlasButtonType.CREATE_CHECKING_PERMISSION,
        AtlasButtonType.CANTON_CSV_DOWNLOAD,
      ].includes(this.buttonType)
    ) {
      return 'atlas-raised-button mat-mdc-raised-button';
    }
    if (
      [AtlasButtonType.FOOTER_NON_EDIT, AtlasButtonType.FOOTER_EDIT_MODE].includes(this.buttonType)
    ) {
      return 'atlas-primary-btn footer-btn';
    }
    return 'atlas-primary-btn';
  }
}
