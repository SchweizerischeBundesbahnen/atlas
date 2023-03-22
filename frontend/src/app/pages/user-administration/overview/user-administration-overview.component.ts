import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../service/user.service';
import { filter, tap } from 'rxjs/operators';
import { TableSettings } from '../../../core/components/table/table-settings';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../../api/model/user';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../core/components/route-to-dialog/route-to-dialog.service';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';
import { ApplicationType, PermissionRestrictionObject, SwissCanton } from '../../../api';
import { SearchType, SearchTypes } from './search-type';
import { Cantons } from '../../tth/overview/canton/Cantons';
import TypeEnum = PermissionRestrictionObject.TypeEnum;

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
  styleUrls: ['./user-administration-overview.component.scss'],
})
export class UserAdministrationOverviewComponent implements OnInit, OnDestroy {
  @ViewChild(TableComponent) tableComponent!: TableComponent<User>;
  userPageResult: { users: User[]; totalCount: number } = { users: [], totalCount: 0 };
  tableIsLoading = false;

  selectedSearch: SearchType = 'USER';
  readonly searchOptions = SearchTypes;

  selectedApplicationOptions: ApplicationType[] = [];
  readonly applicationOptions: ApplicationType[] = Object.values(ApplicationType);
  readonly cantonOptions: SwissCanton[] = Object.values(SwissCanton);
  selectedCantonOptions: SwissCanton[] = [];

  readonly userSearchCtrlName = 'userSearch';
  readonly userSearchForm: FormGroup = new FormGroup({
    [this.userSearchCtrlName]: new FormControl<string | null>(null),
  });
  readonly boSearchCtrlName = 'boSearch';
  readonly boForm: FormGroup = new FormGroup({
    [this.boSearchCtrlName]: new FormControl<string | null>(null),
  });
  readonly tableColumns = tableColumns;
  private readonly dialogClosedEventSubscription: Subscription;

  SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';

  constructor(
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly routeToDialogService: RouteToDialogService
  ) {
    this.dialogClosedEventSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.reloadTableWithCurrentSettings());
  }

  reloadTableWithCurrentSettings(): void {
    if (this.selectedSearch === 'USER') {
      this.checkIfUserExists(
        this.userSearchForm.get(this.userSearchCtrlName)?.value,
        this.tableComponent.paginator.pageIndex
      );
    } else {
      this.filterChanged(this.tableComponent.paginator.pageIndex);
    }
  }

  ngOnInit(): void {
    this.loadUsers({ page: 0, size: 10 });
  }

  ngOnDestroy() {
    this.dialogClosedEventSubscription.unsubscribe();
  }

  openUser(user: User) {
    this.router
      .navigate([user.sbbUserId], {
        relativeTo: this.route,
      })
      .then();
  }

  loadUsers(tableSettings: TableSettings): void {
    this.tableIsLoading = true;
    this.userSearchForm.reset();
    this.boForm.reset();
    this.selectedApplicationOptions = [];
    this.userService
      .getUsers(tableSettings.page, tableSettings.size)
      .pipe(
        tap((result) => {
          this.userPageResult = result;
          this.tableComponent.paginator.pageIndex = tableSettings.page;
          this.tableComponent.paginator.pageSize = tableSettings.size;
          this.tableIsLoading = false;
        })
      )
      .subscribe();
  }

  checkIfUserExists(selectedUser: User, pageIndex = 0): void {
    if (!selectedUser) {
      this.loadUsers({ page: pageIndex, size: this.tableComponent.paginator.pageSize });
    } else if (!selectedUser.sbbUserId) {
      this.userPageResult = { users: [], totalCount: 0 };
      this.tableComponent.paginator.pageIndex = 0;
    } else {
      this.userService
        .hasUserPermissions(selectedUser.sbbUserId)
        .pipe(
          tap((hasPermission) => {
            if (hasPermission) {
              this.userPageResult = { users: [selectedUser], totalCount: 1 };
            } else {
              this.userPageResult = { users: [], totalCount: 0 };
            }
            this.tableComponent.paginator.pageIndex = 0;
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

  filterChanged(pageIndex = 0): void {
    this.tableIsLoading = true;
    const selectedSboid = this.boForm.get(this.boSearchCtrlName)?.value;
    this.userService
      .getUsers(
        pageIndex,
        this.tableComponent.paginator.pageSize,
        new Set<string>([selectedSboid]),
        TypeEnum.BusinessOrganisation,
        new Set<ApplicationType>(this.selectedApplicationOptions)
      )
      .pipe(
        tap((result) => {
          this.userPageResult = result;
          this.tableComponent.paginator.pageIndex = pageIndex;
          this.tableIsLoading = false;
        })
      )
      .subscribe();
  }

  selectedSearchChanged(): void {
    this.ngOnInit();
  }

  getCantonAbbreviation(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }
}
