import { Component, OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { UserService } from '../../service/user.service';
import { filter, tap } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationType, PermissionRestrictionType, SwissCanton, User } from '../../../../api';
import { Subscription } from 'rxjs';
import { tableColumns } from './table-column-definition';
import { SearchType, SearchTypes } from './search-type';
import {
  DetailDialogEvents,
  RouteToDialogService,
} from '../../../../core/components/route-to-dialog/route-to-dialog.service';
import { Cantons } from '../../../tth/overview/canton/Cantons';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
  styleUrls: ['./user-administration-overview.component.scss'],
  providers: [TableService],
})
export class UserAdministrationUserOverviewComponent implements OnDestroy {
  userPageResult: { users: User[]; totalCount: number } = { users: [], totalCount: 0 };
  selectedSearch: SearchType = 'USER';
  readonly searchOptions = SearchTypes;

  selectedApplicationOptions: ApplicationType[] = [];
  readonly applicationBoOptions: ApplicationType[] = [
    ApplicationType.Ttfn,
    ApplicationType.Lidi,
    ApplicationType.Bodi,
  ];
  readonly applicationCantonOptions: ApplicationType[] = [ApplicationType.TimetableHearing];
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
    private readonly routeToDialogService: RouteToDialogService,
    private readonly tableService: TableService
  ) {
    this.dialogClosedEventSubscription = this.routeToDialogService.detailDialogEvent
      .pipe(filter((e) => e === DetailDialogEvents.Closed))
      .subscribe(() => this.reloadTableWithCurrentSettings());
  }

  reloadTableWithCurrentSettings(): void {
    if (this.selectedSearch === 'USER') {
      this.checkIfUserExists(
        this.userSearchForm.get(this.userSearchCtrlName)?.value,
        this.tableService.pageIndex
      );
    } else {
      this.filterChanged(this.tableService.pageIndex);
    }
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

  loadUsers(pagination: TablePagination): void {
    this.userSearchForm.reset();
    this.boForm.reset();
    this.selectedApplicationOptions = [];
    this.userService
      .getUsers(pagination.page, pagination.size)
      .pipe(
        tap((result) => {
          this.userPageResult = result;
          this.tableService.pageIndex = pagination.page;
          this.tableService.pageSize = pagination.size;
        })
      )
      .subscribe();
  }

  checkIfUserExists(selectedUser: User, pageIndex = 0): void {
    if (!selectedUser) {
      this.loadUsers({ page: pageIndex, size: this.tableService.pageSize });
    } else if (!selectedUser.sbbUserId) {
      this.userPageResult = { users: [], totalCount: 0 };
      this.tableService.pageIndex = 0;
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
            this.tableService.pageIndex = 0;
          })
        )
        .subscribe();
    }
  }

  filterChanged(pageIndex = 0): void {
    const selectedSboid = this.boForm.get(this.boSearchCtrlName)?.value;
    this.userService
      .getUsers(
        pageIndex,
        this.tableService.pageSize,
        new Set<string>([selectedSboid, ...this.selectedCantonOptions]),
        this.selectedSearch === 'FILTER'
          ? PermissionRestrictionType.BusinessOrganisation
          : PermissionRestrictionType.Canton,
        new Set<ApplicationType>(this.selectedApplicationOptions)
      )
      .pipe(
        tap((result) => {
          this.userPageResult = result;
          this.tableService.pageIndex = pageIndex;
        })
      )
      .subscribe();
  }

  selectedSearchChanged(): void {
    this.loadUsers({ page: 0, size: 10 });
  }

  getCantonAbbreviation(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }
}
