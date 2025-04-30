import { Component } from '@angular/core';
import { BusinessOrganisationsService, User } from '../../../../../api';
import { UserService } from '../../../service/user.service';
import { UserPermissionManager } from '../../../service/user-permission-manager';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../../pages';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { FormControl, FormGroup } from '@angular/forms';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { MatLabel } from '@angular/material/form-field';
import { UserSelectComponent } from '../../user-select/user-select.component';
import { NgIf, NgFor } from '@angular/common';
import { UserAdministrationReadOnlyDataComponent } from '../../../components/read-only-data/user-administration-read-only-data.component';
import { UserAdministrationApplicationConfigComponent } from '../../../components/application-config/user-administration-application-config.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-user-administration-create',
  templateUrl: './user-administration-user-create.component.html',
  viewProviders: [BusinessOrganisationsService, UserPermissionManager],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    MatLabel,
    UserSelectComponent,
    NgIf,
    UserAdministrationReadOnlyDataComponent,
    NgFor,
    UserAdministrationApplicationConfigComponent,
    DetailFooterComponent,
    TranslatePipe,
  ],
})
export class UserAdministrationUserCreateComponent {
  userLoaded?: User;
  userHasAlreadyPermissions = false;
  selectedUserHasNoUserId = false;
  saveEnabled = true;
  readonly userSearchForm: FormGroup = new FormGroup({
    userSearch: new FormControl<string | null>(null),
  });

  constructor(
    private readonly userService: UserService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialogService: DialogService,
    readonly userPermissionManager: UserPermissionManager
  ) {}

  selectUser(user: User | undefined): void {
    this.selectedUserHasNoUserId = false;
    if (!user?.sbbUserId) {
      this.userHasAlreadyPermissions = false;
      this.userLoaded = undefined;
      if (user) {
        this.selectedUserHasNoUserId = true;
      }
      return;
    }
    this.userService.getUser(user.sbbUserId).subscribe((user) => {
      this.userLoaded = user;
      this.userHasAlreadyPermissions =
        this.userService.getPermissionsFromUserModelAsArray(user).length > 0;
    });
  }

  createUser(): void {
    this.saveEnabled = false;
    this.userPermissionManager.setSbbUserId(this.userLoaded!.sbbUserId!);
    this.userPermissionManager.clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser();
    this.userService
      .createUserPermission(this.userPermissionManager.userPermission)
      .subscribe({
        next: () => {
          this.router
            .navigate(
              [
                Pages.USER_ADMINISTRATION.path,
                this.userPermissionManager.getSbbUserId(),
              ],
              {
                relativeTo: this.route,
              }
            )
            .then(() =>
              this.notificationService.success(
                'USER_ADMIN.NOTIFICATIONS.ADD_SUCCESS'
              )
            );
        },
        error: () => (this.saveEnabled = true),
      });
  }

  cancelCreation(showDialog = true): void {
    if (!showDialog) {
      this.navigateBack();
      return;
    }
    this.dialogService.confirmLeave().subscribe((result) => {
      if (result) {
        this.navigateBack();
      }
    });
  }

  navigateBack() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }
}
