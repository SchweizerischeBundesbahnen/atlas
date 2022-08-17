import { Component, OnInit, ViewChild } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { FormControl, FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { UserService } from '../service/user.service';
import { tap } from 'rxjs/operators';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableComponent } from '../../../core/components/table/table.component';
import { UserModel } from '../../../api';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
})
export class UserAdministrationOverviewComponent implements OnInit {
  @ViewChild(TableComponent) tableComponent!: TableComponent<UserModel>;
  userSearchResults$: Observable<UserModel[]> = of([]);
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

  constructor(private readonly userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers({ page: 0, size: 10 });
  }

  selectOption: (item: UserModel) => string = (item: UserModel): string =>
    `${item.displayName} (${item.mail})`;

  searchUser(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.userSearchResults$ = this.userService.searchUsers(searchQuery);
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
      return;
    }
    if (!selectedUser.sbbUserId) {
      this.userPageResult = { users: [], totalCount: 0 };
      return;
    }
    this.userService
      .getUserPermissions(selectedUser.sbbUserId)
      .pipe(
        tap((userPermissions) => {
          if (userPermissions.length === 0) {
            this.userPageResult = { users: [], totalCount: 0 };
          } else {
            this.userPageResult = { users: [selectedUser], totalCount: 1 };
            this.tableComponent.paginator.pageIndex = 0;
          }
        })
      )
      .subscribe();
  }
}

// TODO: translations vereinfachen
// TODO: unit tests backend + restApidocs (lidi)
// TODO: backend helm, docker, pipeline... + stage yamls
// TODO: rbt groups azure ad connection
// TODO: searchfield update (git merge)
