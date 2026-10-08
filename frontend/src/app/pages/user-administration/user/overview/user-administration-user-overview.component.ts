import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { form } from '@angular/forms/signals';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { ApplicationType, BusinessOrganisation, PermissionRestrictionType, SwissCanton, User } from '../../../../api';
import { tableColumns } from './table-column-definition';
import { SearchType, SearchTypes } from './search-type';
import { Cantons } from '../../../../core/cantons/Cantons';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { TableComponent } from '../../../../core/components/table/table.component';
import { MatLabel } from '@angular/material/form-field';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { UserSelectComponent } from '../user-select/user-select.component';
import { BusinessOrganisationSelectComponent } from '../../../../core/form-components/bo-select/business-organisation-select.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'atlas-user-administration-overview',
  templateUrl: './user-administration-user-overview.component.html',
  styleUrls: ['./user-administration-user-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    TableComponent,
    MatLabel,
    MatRadioGroup,
    UserSelectComponent,
    BusinessOrganisationSelectComponent,
    SelectComponent,
    RouterOutlet,
    TranslatePipe,
    MatRadioButton,
  ],
})
export class UserAdministrationUserOverviewComponent {
  readonly userPageResult = signal<{ users: User[]; totalCount: number }>({
    users: [],
    totalCount: 0,
  });
  readonly selectedSearch = signal<SearchType>('USER');
  readonly searchOptions = SearchTypes;

  readonly selectedApplicationOptions = signal<ApplicationType[]>([]);
  readonly applicationBoOptions: ApplicationType[] = [
    ApplicationType.Ttfn,
    ApplicationType.Lidi,
    ApplicationType.Bodi,
    ApplicationType.Sepodi,
    ApplicationType.Prm,
  ];
  readonly applicationCantonOptions: ApplicationType[] = [ApplicationType.TimetableHearing];
  readonly cantonOptions: SwissCanton[] = Object.values(SwissCanton);
  readonly selectedCantonOptions = signal<SwissCanton[]>([]);

  readonly userSearchModel = signal<{ userSearch: User | null }>({ userSearch: null });
  readonly userSearchForm = form(this.userSearchModel);
  readonly boSearchModel = signal<{ boSearch: BusinessOrganisation | string | null }>({ boSearch: null });
  readonly boSearchForm = form(this.boSearchModel);
  readonly tableColumns = tableColumns;

  SWISS_CANTONS_PREFIX_LABEL = 'TTH.CANTON.';

  private readonly userAdministrationService = inject(UserAdministrationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tableService = inject(TableService);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private usersSubscription?: Subscription;

  readonly copyingEmails = signal(false);

  readonly hasFilterCriteria = computed(
    () =>
      this.selectedApplicationOptions().length > 0 ||
      this.selectedCantonOptions().length > 0 ||
      !!this.boSearchForm.boSearch().value()
  );

  reloadTableWithCurrentSettings(): void {
    if (this.selectedSearch() === 'USER') {
      this.onUserFilterChanged(this.userSearchForm.userSearch().value(), this.tableService.pageIndex);
    } else {
      this.filterChanged(this.tableService.pageIndex);
    }
  }

  openUser(user: User) {
    this.router
      .navigate([user.sbbUserId], {
        relativeTo: this.route,
      })
      .then();
  }

  loadUsers(pagination: TablePagination): void {
    this.usersSubscription?.unsubscribe();
    this.userSearchForm.userSearch().value.set(null);
    this.boSearchForm.boSearch().value.set(null);
    this.selectedApplicationOptions.set([]);
    this.selectedCantonOptions.set([]);
    this.usersSubscription = this.userAdministrationService
      .getUsers(pagination.page, pagination.size)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        this.userPageResult.set({
          users: result.objects!,
          totalCount: result.totalCount!,
        });
        this.tableService.pageIndex = pagination.page;
        this.tableService.pageSize = pagination.size;
      });
  }

  onUserFilterChanged(selectedUser: User | null | undefined, pageIndex = 0): void {
    if (!selectedUser) {
      this.loadUsers({ page: pageIndex, size: this.tableService.pageSize });
    } else if (!selectedUser.sbbUserId) {
      this.usersSubscription?.unsubscribe();
      this.userPageResult.set({ users: [], totalCount: 0 });
      this.tableService.pageIndex = 0;
    } else {
      this.usersSubscription?.unsubscribe();
      this.usersSubscription = this.userAdministrationService
        .getUser(selectedUser.sbbUserId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((user) => {
          const hasPermissions = Array.from(user.permissions).length > 0;
          this.userPageResult.set({
            users: hasPermissions ? [user] : [],
            totalCount: hasPermissions ? 1 : 0,
          });
          this.tableService.pageIndex = 0;
        });
    }
  }

  filterChanged(pageIndex = 0): void {
    const { permissionRestrictions, type } = this.currentFilter();
    this.usersSubscription?.unsubscribe();
    this.usersSubscription = this.userAdministrationService
      .getUsers(
        pageIndex,
        this.tableService.pageSize,
        permissionRestrictions,
        type,
        new Set<ApplicationType>(this.selectedApplicationOptions())
      )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        this.userPageResult.set({
          users: result.objects!,
          totalCount: result.totalCount!,
        });
        this.tableService.pageIndex = pageIndex;
      });
  }

  copyFilteredEmails(): void {
    const { permissionRestrictions, type } = this.currentFilter();
    this.copyingEmails.set(true);
    this.userAdministrationService
      .getUserEmails(permissionRestrictions, type, new Set<ApplicationType>(this.selectedApplicationOptions()))
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.copyingEmails.set(false))
      )
      .subscribe({
        next: (emails) => this.copyEmailsToClipboard(emails),
        error: (error: HttpErrorResponse) => this.notificationService.error(error),
      });
  }

  selectedSearchChanged(search: SearchType): void {
    this.selectedSearch.set(search);
    this.loadUsers({ page: 0, size: 10 });
  }

  readonly getCantonAbbreviation = (canton: SwissCanton) => Cantons.fromSwissCanton(canton)?.short;

  applicationChanged(applicationTypes: ApplicationType[]) {
    this.selectedApplicationOptions.set(applicationTypes);
  }

  cantonChanged(cantons: SwissCanton[]) {
    this.selectedCantonOptions.set(cantons);
  }

  private currentFilter(): { permissionRestrictions: Set<string>; type: PermissionRestrictionType } {
    const selectedBo = this.boSearchForm.boSearch().value();
    const selectedSboid = typeof selectedBo === 'string' ? selectedBo : (selectedBo?.sboid ?? null);
    return {
      permissionRestrictions: new Set<string>([
        ...(selectedSboid ? [selectedSboid] : []),
        ...this.selectedCantonOptions(),
      ]),
      type:
        this.selectedSearch() === 'FILTER'
          ? PermissionRestrictionType.BusinessOrganisation
          : PermissionRestrictionType.Canton,
    };
  }

  private copyEmailsToClipboard(emails: string[]): void {
    if (emails.length === 0) {
      this.notificationService.info('USER_ADMIN.COPY_EMAILS_EMPTY');
      return;
    }
    void navigator.clipboard.writeText(emails.join('; ')).then(() => {
      this.notificationService.success('COMMON.COPY_CLIPBOARD_SUCCESS');
    });
  }
}
