import { Component, inject, input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  BusinessOrganisationsService,
  ClientCredential,
  Permission,
} from '../../../../../api';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { CreationEditionRecord } from '../../../../../core/components/base-detail/user-edit-info/creation-edition-record';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionComponent } from '../../../../../core/components/permissions/permission.component';
import { BackButtonDirective } from '../../../../../core/components/button/back-button/back-button.directive';
import { UserPermissionGivenClientService } from './user-permission-given-client.service';
import { ApplicationPermissionFormGroupBuilder } from '../../../../../core/components/permissions/form/application-permission-form-group';
import { ClientCredentialAdministrationService } from '../../../../../api/service/user-administration/client-credential-administration.service';

@Component({
  selector: 'app-client-credential-administration-edit',
  templateUrl: './user-administration-client-edit.component.html',
  styleUrls: ['./user-administration-client-edit.component.scss'],
  viewProviders: [BusinessOrganisationsService],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    UserDetailInfoComponent,
    DetailFooterComponent,
    TranslatePipe,
    PermissionComponent,
    BackButtonDirective,
  ],
})
export class UserAdministrationClientEditComponent implements OnInit {
  client = input.required<ClientCredential>();

  editMode = false;
  saveEnabled = true;
  record!: CreationEditionRecord;

  userPermissionGivenClientService = inject(UserPermissionGivenClientService);
  notificationService = inject(NotificationService);
  clientCredentialAdministrationService = inject(
    ClientCredentialAdministrationService
  );
  dialogService = inject(DialogService);

  ngOnInit() {
    this.userPermissionGivenClientService.clientCredential = this.client();
    const permissionsFromUserModelAsArray = Array.from(
      this.client().permissions!
    );
    this.convertPermissionToRecord(permissionsFromUserModelAsArray);
  }

  saveClientCredential(): void {
    this.saveEnabled = false;
    this.formGroup.disable();

    const permission = ApplicationPermissionFormGroupBuilder.formToModel(
      this.formGroup
    );
    this.clientCredentialAdministrationService
      .updateClientCredentialPermissions(
        this.client().clientCredentialId!,
        permission.application,
        permission
      )
      .subscribe({
        next: () => {
          this.editMode = false;
          this.notificationService.success(
            'USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS'
          );
        },
        error: () => (this.saveEnabled = true),
      });
  }

  private convertPermissionToRecord(permissions: Permission[]): void {
    if (permissions.length > 0) {
      this.record = {
        editor: permissions[0].editor,
        editionDate: permissions[0].editionDate,
        creator: permissions[0].creator,
        creationDate: permissions[0].creationDate,
      };
    }
  }

  toggleEdit() {
    if (this.formGroup.disabled) {
      this.formGroup.enable();
      this.editMode = true;
    } else {
      this.dialogService.confirmLeave().subscribe((result) => {
        if (result) {
          this.editMode = false;
          this.userPermissionGivenClientService.loadFormGroup(
            this.userPermissionGivenClientService.getCurrentForm()!.controls
              .application.value!
          );
        }
      });
    }
  }

  get formGroup() {
    return this.userPermissionGivenClientService
      .applicationPermissionFormGroup!;
  }
}
