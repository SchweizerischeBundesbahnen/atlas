import {Component, ContentChild, EventEmitter, Input, Output, TemplateRef} from '@angular/core';
import {ApplicationType} from '../../../api';
import {AtlasButtonType} from './atlas-button.type';
import {NON_PROD_STAGES} from '../../constants/stages';
import {environment} from '../../../../environments/environment';
import {Countries} from '../../country/Countries';
import {PermissionService} from "../../auth/permission/permission.service";

@Component({
    selector: 'atlas-button[buttonType]',
    templateUrl: './atlas-button.component.html',
    standalone: false
})
export class AtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() businessOrganisations: string[] = [];
  @Input() canton!: string;
  @Input() uicCountryCode?: number;
  @Input() disabled!: boolean;

  @Input() wrapperStyleClass!: string;
  @Input() buttonDataCy!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() footerEdit = false;
  @Input() submitButton!: boolean;
  @Input() buttonText!: string;
  @Input() title!: string;
  @Input() buttonStyleClass: string | undefined;

  @Output() buttonClicked = new EventEmitter<void>();
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('rightIcon') rightIcon!: TemplateRef<any>;

  constructor(private permissionService: PermissionService) {}

  isButtonVisible() {
    if (this.buttonType === AtlasButtonType.CREATE_CHECKING_PERMISSION) {
      return this.mayCreate();
    }
    if (this.buttonType === AtlasButtonType.EDIT) {
      return this.mayEdit();
    }
    if (this.buttonType === AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT) {
      return this.mayEditServicePointDependentObject();
    }
    if (
      [
        AtlasButtonType.REVOKE,
        AtlasButtonType.SKIP_WORKFLOW,
        AtlasButtonType.SUPERVISOR_BUTTON,
        AtlasButtonType.MANAGE_TIMETABLE_HEARING,
      ].includes(this.buttonType)
    ) {
      return this.isAtLeastSupervisor();
    }
    if (this.buttonType === AtlasButtonType.DELETE) {
      return this.mayDelete();
    }
    if (this.buttonType === AtlasButtonType.CANTON_WRITE_PERMISSION) {
      return this.hasWritePermissionsForCanton();
    }
    if ([AtlasButtonType.WHITE_FOOTER_NON_EDIT].includes(this.buttonType)) {
      return !this.footerEdit;
    }
    if ([AtlasButtonType.WHITE_FOOTER_EDIT_MODE].includes(this.buttonType)) {
      return this.footerEdit;
    }
    return true;
  }

  mayCreate() {
    if (!this.applicationType) {
      throw new Error('Permission checking button needs applicationtype');
    }
    return this.permissionService.hasPermissionsToCreate(this.applicationType);
  }

  mayEdit() {
    if (!this.applicationType) {
      throw new Error('Edit button needs applicationType');
    }
    if (this.applicationType !== ApplicationType.Bodi && !this.businessOrganisation) {
      throw new Error('Edit button needs businessOrganisation');
    }
    if (this.uicCountryCode) {
      return this.mayEditWithUicCountryCode();
    }
    return this.permissionService.hasPermissionsToWrite(this.applicationType, this.businessOrganisation);
  }

  private mayEditWithUicCountryCode() {
    return (
      this.permissionService.hasPermissionsToWrite(this.applicationType, this.businessOrganisation) &&
      this.permissionService.hasPermissionsToWrite(
        this.applicationType,
        Countries.fromUicCode(this.uicCountryCode!).enumCountry,
      )
    );
  }

  isAtLeastSupervisor(): boolean {
    if (!this.applicationType) {
      throw new Error('Revoke button needs applicationtype');
    }
    return this.permissionService.isAtLeastSupervisor(this.applicationType);
  }

  mayDelete(): boolean {
    return this.permissionService.isAdmin && NON_PROD_STAGES.includes(environment.label);
  }

  hasWritePermissionsForCanton() {
    return this.permissionService.hasWritePermissionsToForCanton(this.applicationType, this.canton);
  }

  getButtonStyleClass() {
    if (this.buttonStyleClass) {
      return this.buttonStyleClass;
    }
    if (this.buttonType === AtlasButtonType.DEFAULT_PRIMARY) {
      return 'atlas-primary-btn';
    }
    if (this.buttonType === AtlasButtonType.ICON) {
      return 'atlas-icon-btn';
    }
    if (
      [
        AtlasButtonType.CREATE,
        AtlasButtonType.CREATE_CHECKING_PERMISSION,
        AtlasButtonType.CANTON_WRITE_PERMISSION,
        AtlasButtonType.MANAGE_TIMETABLE_HEARING,
      ].includes(this.buttonType)
    ) {
      return 'atlas-raised-button mat-mdc-raised-button';
    }
    if (this.buttonType === AtlasButtonType.CONFIRM) {
      return 'atlas-primary-btn primary-color-btn';
    }
    return 'atlas-primary-btn';
  }

  private mayEditServicePointDependentObject() {
    return this.businessOrganisations
      .map((organisation) =>
        this.permissionService.hasPermissionsToWrite(this.applicationType, organisation),
      )
      .includes(true);
  }
}
