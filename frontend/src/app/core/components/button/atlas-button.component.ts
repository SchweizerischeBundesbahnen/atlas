import { Component, ContentChild, inject, input, output, TemplateRef } from '@angular/core';
import { ApplicationType } from '../../../api';
import { AtlasButtonType } from './atlas-button.type';
import { NON_PROD_STAGES } from '../../constants/stages';
import { environment } from '../../../../environments/environment';
import { Countries } from '../../country/Countries';
import { PermissionService } from '../../auth/permission/permission.service';
import { NgClass, NgTemplateOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-button',
  templateUrl: './atlas-button.component.html',
  imports: [NgClass, NgTemplateOutlet, TranslatePipe],
  providers: [TranslatePipe],
})
export class AtlasButtonComponent {
  readonly applicationType = input<ApplicationType>();
  readonly businessOrganisation = input<string>();
  readonly businessOrganisations = input<string[]>([]);
  readonly canton = input<string>();
  readonly uicCountryCode = input<number>();
  readonly disabled = input(false);

  readonly wrapperStyleClass = input('');
  readonly buttonDataCy = input<string>();
  readonly buttonType = input.required<AtlasButtonType>();
  readonly footerEdit = input(false);
  readonly submitButton = input(false);
  readonly buttonText = input<string>();
  readonly title = input<string>('');
  readonly buttonStyleClass = input<string>();

  readonly buttonClicked = output<void>();
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('rightIcon') rightIcon!: TemplateRef<any>;

  private readonly permissionService = inject(PermissionService);

  isButtonVisible() {
    if (this.buttonType() === AtlasButtonType.CREATE_CHECKING_PERMISSION) {
      return this.mayCreate();
    }
    if (this.buttonType() === AtlasButtonType.EDIT) {
      return this.mayEdit();
    }
    if (this.buttonType() === AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT) {
      return this.mayEditServicePointDependentObject();
    }
    if (
      (
        [
          AtlasButtonType.REVOKE,
          AtlasButtonType.SKIP_WORKFLOW,
          AtlasButtonType.SUPERVISOR_BUTTON,
          AtlasButtonType.MANAGE_TIMETABLE_HEARING,
        ] as readonly AtlasButtonType[]
      ).includes(this.buttonType())
    ) {
      return this.isAtLeastSupervisor();
    }
    if (this.buttonType() === AtlasButtonType.DELETE) {
      return this.mayDelete();
    }
    if (this.buttonType() === AtlasButtonType.CANTON_WRITE_PERMISSION) {
      return this.hasWritePermissionsForCanton();
    }
    if (AtlasButtonType.WHITE_FOOTER_NON_EDIT === this.buttonType()) {
      return !this.footerEdit();
    }
    if (AtlasButtonType.WHITE_FOOTER_EDIT_MODE === this.buttonType()) {
      return this.footerEdit();
    }
    return true;
  }

  mayCreate() {
    if (!this.applicationType()) {
      throw new Error('Permission checking button needs applicationtype');
    }
    return this.permissionService.hasPermissionsToCreate(this.applicationType()!);
  }

  mayEdit() {
    if (!this.applicationType()) {
      throw new Error('Edit button needs applicationType');
    }
    const businessOrganisation = this.businessOrganisation();
    if (this.applicationType() !== ApplicationType.Bodi && !businessOrganisation) {
      throw new Error('Edit button needs businessOrganisation');
    }
    if (this.uicCountryCode()) {
      return this.mayEditWithUicCountryCode();
    }
    return this.permissionService.hasPermissionsToWrite(this.applicationType()!, businessOrganisation);
  }

  private mayEditWithUicCountryCode() {
    return (
      this.permissionService.hasPermissionsToWrite(this.applicationType()!, this.businessOrganisation()) &&
      this.permissionService.hasPermissionsToWrite(
        this.applicationType()!,
        Countries.fromUicCode(this.uicCountryCode()!).enumCountry
      )
    );
  }

  isAtLeastSupervisor(): boolean {
    if (!this.applicationType()) {
      throw new Error('Revoke button needs applicationtype');
    }
    return this.permissionService.isAtLeastSupervisor(this.applicationType()!);
  }

  mayDelete(): boolean {
    return this.permissionService.isAdmin && NON_PROD_STAGES.includes(environment.label);
  }

  hasWritePermissionsForCanton() {
    return this.permissionService.hasWritePermissionsToForCanton(this.applicationType()!, this.canton());
  }

  getButtonStyleClass() {
    const buttonStyleClass = this.buttonStyleClass();
    if (buttonStyleClass) {
      return buttonStyleClass;
    }
    if (this.buttonType() === AtlasButtonType.DEFAULT_PRIMARY) {
      return 'atlas-primary-btn';
    }
    if (this.buttonType() === AtlasButtonType.ICON) {
      return 'atlas-icon-btn';
    }
    if (
      (
        [
          AtlasButtonType.CREATE,
          AtlasButtonType.CREATE_CHECKING_PERMISSION,
          AtlasButtonType.CANTON_WRITE_PERMISSION,
          AtlasButtonType.MANAGE_TIMETABLE_HEARING,
        ] as readonly AtlasButtonType[]
      ).includes(this.buttonType())
    ) {
      return 'atlas-raised-button mat-mdc-raised-button';
    }
    if (this.buttonType() === AtlasButtonType.CONFIRM) {
      return 'atlas-primary-btn primary-color-btn';
    }
    return 'atlas-primary-btn';
  }

  private mayEditServicePointDependentObject() {
    return this.businessOrganisations()
      .map((organisation) => this.permissionService.hasPermissionsToWrite(this.applicationType()!, organisation))
      .includes(true);
  }
}
