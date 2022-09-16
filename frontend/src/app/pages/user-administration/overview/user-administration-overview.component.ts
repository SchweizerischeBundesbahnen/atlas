import { Component, OnInit, ViewChild } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../service/user.service';
import { tap } from 'rxjs/operators';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableComponent } from '../../../core/components/table/table.component';
import { UserModel } from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
})
export class UserAdministrationOverviewComponent implements OnInit {
  @ViewChild(TableComponent) tableComponent!: TableComponent<UserModel>;
  userPageResult: { users: UserModel[]; totalCount: number } = { users: [], totalCount: 0 };
  tableIsLoading = false;
  readonly tableColumns: TableColumn<UserModel>[] = [
    {
      headerTitle: 'USER_ADMIN.LAST_NAME',
      value: 'lastName',
    },
    {
      headerTitle: 'USER_ADMIN.FIRST_NAME',
      value: 'firstName',
    },
    {
      headerTitle: 'USER_ADMIN.MAIL',
      value: 'mail',
    },
    {
      headerTitle: 'USER_ADMIN.USER_ID',
      value: 'sbbUserId',
    },
    {
      headerTitle: 'USER_ADMIN.ACCOUNT_STATUS',
      value: 'accountStatus',
      translate: {
        withPrefix: 'USER_ADMIN.ACCOUNT_STATUS_TYPE.',
      },
    },
  ];
  readonly form: FormGroup = new FormGroup({
    userSearch: new FormControl<string | null>(null),
  });

  constructor(
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadUsers({ page: 0, size: 10 });
  }

  openUser(user: UserModel) {
    this.router
      .navigate([user.sbbUserId], {
        relativeTo: this.route,
      })
      .then();
  }

  loadUsers(tableSettings: TableSettings): void {
    this.tableIsLoading = true;
    this.form.reset();
    this.userService
      .getUsers(tableSettings.page, tableSettings.size)
      .pipe(
        tap((result) => {
          this.userPageResult = result;
          this.tableIsLoading = false;
        })
      )
      .subscribe();
  }

  checkIfUserExists(selectedUser: UserModel): void {
    if (!selectedUser) {
      this.loadUsers({ page: 0, size: this.tableComponent.paginator.pageSize });
    } else if (!selectedUser.sbbUserId) {
      this.userPageResult = { users: [], totalCount: 0 };
    } else {
      this.userService
        .hasUserPermissions(selectedUser.sbbUserId)
        .pipe(
          tap((hasPermission) => {
            if (hasPermission) {
              this.userPageResult = { users: [selectedUser], totalCount: 1 };
              this.tableComponent.paginator.pageIndex = 0;
            } else {
              this.userPageResult = { users: [], totalCount: 0 };
            }
          })
        )
        .subscribe();
    }
  }

  routeToCreate(): Promise<boolean> {
    return this.router.navigate(['add'], {
      relativeTo: this.route,
    });
  }
}
