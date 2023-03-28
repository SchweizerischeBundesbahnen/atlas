import { Component } from '@angular/core';
import { BusinessOrganisationsService, ClientCredentialPermissionCreate } from '../../../../../api';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ClientCredentialCreateFormGroup } from './create-form-group';
import { WhitespaceValidator } from '../../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../../../core/validation/field-lengths/atlas-field-length-validator';

@Component({
  selector: 'app-client-credential-administration-create',
  templateUrl: './user-administration-client-create.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
})
export class UserAdministrationClientCreateComponent {
  saveEnabled = true;

  form = new FormGroup<ClientCredentialCreateFormGroup>({
    clientCredentialId: new FormControl('', [
      Validators.required,
      AtlasCharsetsValidator.iso88591,
      AtlasFieldLengthValidator.length_50,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    alias: new FormControl('', [
      Validators.required,
      AtlasCharsetsValidator.iso88591,
      AtlasFieldLengthValidator.length_100,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    comment: new FormControl('', [
      AtlasCharsetsValidator.iso88591,
      AtlasFieldLengthValidator.length_100,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
  });

  constructor(
    private readonly userService: UserService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialogService: DialogService,
    private readonly dialogRef: MatDialogRef<any>,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  create(): void {
    this.saveEnabled = false;
    this.userPermissionManager.clearPermissionRestrictionsIfNotWriter();
    const managedPermission = this.userPermissionManager.userPermission;
    const permission = {
      ...this.form.value,
      ...managedPermission,
    } as ClientCredentialPermissionCreate;
    this.userService.createClientCredentialPermission(permission).subscribe({
      next: () => {
        this.router
          .navigate([Pages.USER_ADMINISTRATION.path, this.userPermissionManager.getSbbUserId()], {
            relativeTo: this.route,
          })
          .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
      },
      error: () => (this.saveEnabled = true),
    });
  }

  cancelCreation(): void {
    if (this.form.untouched) {
      this.dialogRef.close();
    } else {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.dialogRef.close();
        }
      });
    }
  }
}
