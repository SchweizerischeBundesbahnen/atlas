import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../service/user.service';
import { filter, tap } from 'rxjs/operators';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, Router } from '@angular/router';
import { UserModel } from '../../../api/model/userModel';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
})
export class UserAdministrationOverviewComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent) tableComponent!: TableComponent<UserModel>;
  userPageResult: { users: UserModel[]; totalCount: number } = { users: [], totalCount: 0 };
  tableIsLoading = false;

  readonly userSearchForm: FormGroup = new FormGroup({
    userSearch: new FormControl<string | null>(null),
  });
  readonly tableColumns = tableColumns;
  private readonly dialogClosedEventSubscription: Subscription;

  constructor(
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly routeToDialogService: RouteToDialogService
  ) {
    this.dialogClosedEventSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.ngOnInit());
  }

  ngOnInit(): void {
    this.loadUsers({ page: 0, size: 10 });
  }

  ngOnDestroy() {
    this.dialogClosedEventSubscription.unsubscribe();
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
    this.userSearchForm.reset();
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
