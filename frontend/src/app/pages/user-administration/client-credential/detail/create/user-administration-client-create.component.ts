import { Component } from '@angular/core';
import { BusinessOrganisationsService, ClientCredentialPermissionCreate } from '../../../../../api';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ClientCredentialCreateFormGroup } from './create-form-group';
import { WhitespaceValidator } from '../../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../../core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from '../../../../../core/validation/field-lengths/atlas-field-length-validator';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { NgFor } from '@angular/common';
import { UserAdministrationApplicationConfigComponent } from '../../../components/application-config/user-administration-application-config.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-client-credential-administration-create',
    templateUrl: './user-administration-client-create.component.html',
    viewProviders: [BusinessOrganisationsService, UserPermissionManager],
    imports: [ScrollToTopDirective, DetailPageContainerComponent, DetailPageContentComponent, TextFieldComponent, ReactiveFormsModule, NgFor, UserAdministrationApplicationConfigComponent, DetailFooterComponent, TranslatePipe]
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
    readonly userPermissionManager: UserPermissionManager
  ) {}

  create(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.saveEnabled = false;
      this.userPermissionManager.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser();
      const managedPermission = this.userPermissionManager.userPermission;
      const permission = {
        ...this.form.value,
        ...managedPermission,
      } as ClientCredentialPermissionCreate;
      this.userService.createClientCredentialPermission(permission).subscribe({
        next: () => {
          this.router
            .navigate(
              [Pages.USER_ADMINISTRATION.path, Pages.CLIENTS.path, permission.clientCredentialId],
              {
                relativeTo: this.route,
              }
            )
            .then(() => this.notificationService.success('USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'));
        },
        error: () => (this.saveEnabled = true),
      });
    }
  }

  cancelCreation(): void {
    if (this.form.untouched) {
      this.back();
    } else {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.back();
        }
      });
    }
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }
}
