import { Component, inject, Input, OnInit } from '@angular/core';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { UserService } from '../../../service/user.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { CreationEditionRecord } from '../../../../../core/components/base-detail/user-edit-info/creation-edition-record';
import moment from 'moment';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ApplicationType,
  User,
  UserPermissionCreate,
} from '../../../../../api';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { BackButtonDirective } from '../../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionComponent } from '../../../../../core/components/permissions/permission.component';
import { UserPermissionGivenUserService } from './user-permission-given-user.service';
import { ApplicationPermissionFormGroupBuilder } from '../../../../../core/components/permissions/form/application-permission-form-group';
import { UserAdministrationService } from '../../../../../api/service/user-administration/user-administration.service';

@Component({
  selector: 'app-user-administration-edit',
  templateUrl: './user-administration-user-edit.component.html',
  viewProviders: [UserPermissionManager],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    UserDetailInfoComponent,
    DetailFooterComponent,
    BackButtonDirective,
    TranslatePipe,
    PermissionComponent,
  ],
})
export class UserAdministrationUserEditComponent implements OnInit {
  @Input() user!: User;
  userRecord?: CreationEditionRecord;

  constructor(
    private readonly notificationService: NotificationService,
    private readonly userService: UserService,
    private readonly dialogService: DialogService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  userPermissionGivenUserService = inject(UserPermissionGivenUserService);
  userAdministrationService = inject(UserAdministrationService);

  ngOnInit() {
    this.userPermissionGivenUserService.user = this.user!;
    this.convertUserPermissionToRecord();
  }

  save(): void {
    this.userPermissionGivenUserService.applicationPermissionFormGroup?.disable();

    const userPermission = ApplicationPermissionFormGroupBuilder.formToModel(
      this.formGroup!
    );
    this.userAdministrationService
      .updateUserPermission(
        this.user.userId!,
        userPermission.application,
        userPermission
      )
      .subscribe({
        next: (user: User) => {
          this.user = user;
          this.ngOnInit();
          this.notificationService.success(
            'USER_ADMIN.NOTIFICATIONS.EDIT_SUCCESS'
          );
        },
        error: () => this.formGroup?.enable(),
      });
  }

  private convertUserPermissionToRecord(): void {
    const permissionsFromUserModelAsArray =
      this.userService.getPermissionsFromUserModelAsArray(this.user!);
    if (permissionsFromUserModelAsArray.length > 0) {
      const firstCreated = permissionsFromUserModelAsArray.reduce(
        (previousValue, currentValue) => {
          return moment(new Date(previousValue.creationDate!)).isBefore(
            moment(new Date(currentValue.creationDate!))
          )
            ? previousValue
            : currentValue;
        }
      );
      const lastEdited = permissionsFromUserModelAsArray.reduce(
        (previousValue, currentValue) => {
          return moment(new Date(previousValue.editionDate!)).isAfter(
            moment(new Date(currentValue.editionDate!))
          )
            ? previousValue
            : currentValue;
        }
      );
      this.userRecord = {
        editor: lastEdited.editor,
        editionDate: lastEdited.editionDate,
        creator: firstCreated.creator,
        creationDate: firstCreated.creationDate,
      };
    }
  }

  toggleEdit() {
    if (this.formGroup?.disabled) {
      this.formGroup?.enable();
    } else {
      this.formGroup?.disable();
    }
  }

  get formGroup() {
    return this.userPermissionGivenUserService.applicationPermissionFormGroup;
  }
}
