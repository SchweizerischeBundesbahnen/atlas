import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';
import { TimetableHearingYearInternalService } from '../../../api/service/lidi/timetable-hearing-year-internal.service';
import { HearingStatus, TimetableHearingYear } from '../../../api';
import { Cantons } from '../../../core/cantons/Cantons';
import { TthUtils } from '../util/tth-utils';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Pages } from '../../pages';
import { OverviewTabHeadingComponent } from '../overview-tab/overview-tab-heading/overview-tab-heading.component';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { NgOptimizedImage } from '@angular/common';
import { TableService } from '../../../core/components/table/table.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'atlas-tth-overview-base',
  imports: [OverviewTabHeadingComponent, SelectComponent, NgOptimizedImage, RouterOutlet],
  templateUrl: './tth-overview-base.component.html',
})
export class TthOverviewBaseComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly overviewToTabService = inject(OverviewToTabShareDataService);
  private readonly timetableHearingYearsService = inject(TimetableHearingYearInternalService);
  private readonly tableService = inject(TableService);

  readonly cantonShort = this.overviewToTabService.cantonShort;
  readonly timetableYear = this.overviewToTabService.timetableYear;
  readonly hearingStatus = this.overviewToTabService.hearingStatus;
  readonly isPlannedTimetableHearingYearFound = this.overviewToTabService.isPlannedTimetableHearingYearFound;
  readonly isTimetableHearingYearFound = this.overviewToTabService.isTimetableHearingYearFound;
  readonly isHearingYearActive = this.overviewToTabService.isHearingYearActive;
  readonly isHearingYearPlanned = this.overviewToTabService.isHearingYearPlanned;
  readonly isHearingYearArchived = this.overviewToTabService.isHearingYearArchived;
  readonly yearSelection = this.overviewToTabService.yearSelection;

  YEAR_DROPDOWN_OPTIONS: number[] = [];

  CANTON_DROPDOWN_OPTIONS = Cantons.cantonsWithSwiss.map((value) => value.short);
  defaultDropdownCantonSelection = this.CANTON_DROPDOWN_OPTIONS[0];

  ngOnInit(): void {
    this.init();
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        this.initTable();
      });
  }

  init(): void {
    this.overviewToTabService.setHearingStatus(this.route.snapshot.data.hearingStatus);
    this.checkIfRoutedCantonExists();
    this.defaultDropdownCantonSelection = this.initDefaultDropdownCantonSelection();

    this.initTable();
  }

  initTable() {
    if (this.isHearingYearActive()) {
      this.initOverviewActiveTable();
    } else if (this.isHearingYearPlanned()) {
      this.initOverviewPlannedTable();
    } else {
      this.initOverviewArchivedTable();
    }
  }

  changeSelectedCantonFromDropdown(selectedCanton: string): void {
    const canton = selectedCanton.toLowerCase();
    this.overviewToTabService.setCantonShort(canton);
    this.navigateTo(canton, this.timetableYear().timetableYear);
    this.tableService.resetTableSettings();
  }

  changeSelectedYearFromDropdown(selectedYear: number): void {
    this.overviewToTabService.setYearSelection(selectedYear);
    this.navigateTo(this.cantonShort().toLowerCase(), selectedYear);
    this.tableService.resetTableSettings();
  }

  navigateTo(canton: string, timetableYear: number): void {
    const currentUrl = this.router.url;
    let currentView = Pages.TTH_STATEMENTS.path;

    if (currentUrl.includes('/' + Pages.TTH_DOSSIERS.path)) {
      currentView = Pages.TTH_DOSSIERS.path;
    }
    this.router.navigate([Pages.TTH.path, canton.toLowerCase(), this.hearingStatus().toLowerCase(), currentView], {
      queryParams: { year: timetableYear },
      queryParamsHandling: 'merge',
    });
  }

  checkIfRoutedCantonExists(): void {
    const swissCantonEnum = Cantons.getSwissCantonEnum(this.cantonShort());
    if (!swissCantonEnum) {
      this.overviewToTabService.setTimetableHearingYearFound(false);
      this.router.navigate([Pages.TTH.path]).then();
    }
  }

  private initDefaultDropdownCantonSelection(): string {
    return this.CANTON_DROPDOWN_OPTIONS[
      this.CANTON_DROPDOWN_OPTIONS.findIndex((value) => value.toLowerCase() === this.cantonShort().toLowerCase())
    ];
  }

  private initOverviewActiveTable(): void {
    this.overviewToTabService.setTimetableHearingYearLoading(true);

    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Active])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length === 0) {
          this.overviewToTabService.setTimetableHearingYearFound(false);
          this.getPlannedTimetableYearWhenNoActiveFound();
        } else {
          this.overviewToTabService.setTimetableHearingYearFound(true);
          this.overviewToTabService.setTimetableHearingYear(timetableHearingYears[0]);
          this.overviewToTabService.setTimetableHearingYearLoading(false);
        }
      });
  }

  private getPlannedTimetableYearWhenNoActiveFound(): void {
    this.timetableHearingYearsService
      .getHearingYears([HearingStatus.Planned])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears && timetableHearingYears?.length >= 1) {
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(timetableHearingYears, false);
          this.overviewToTabService.setTimetableHearingYear(foundTimetableHearingYears[0]);
          this.overviewToTabService.setPlannedTimetableHearingYearFound(true);
        } else {
          this.overviewToTabService.setPlannedTimetableHearingYearFound(false);
          this.overviewToTabService.setTimetableHearingYearFound(false);
        }
      });
  }

  initOverviewPlannedTable(): void {
    this.getTimetableHearingYear(HearingStatus.Planned, false);
  }

  initOverviewArchivedTable(): void {
    this.getTimetableHearingYear(HearingStatus.Archived, true);
  }

  private getTimetableHearingYear(hearingStatus: HearingStatus, sortReverse: boolean): void {
    this.overviewToTabService.setTimetableHearingYearLoading(true);

    this.timetableHearingYearsService
      .getHearingYears([hearingStatus])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((timetableHearingYears) => {
        if (timetableHearingYears.length === 0) {
          this.overviewToTabService.setTimetableHearingYearFound(false);
        } else {
          this.overviewToTabService.setTimetableHearingYearFound(true);
          const foundTimetableHearingYears = TthUtils.sortByTimetableHearingYear(timetableHearingYears, sortReverse);
          this.setFoundHearingYear(foundTimetableHearingYears);
        }
        this.overviewToTabService.setTimetableHearingYearLoading(false);
      });
  }

  private setFoundHearingYear(timetableHearingYears: TimetableHearingYear[]): void {
    this.YEAR_DROPDOWN_OPTIONS = timetableHearingYears.map((value) => value.timetableYear);

    const paramYear = this.route.snapshot.queryParams.year;
    if (paramYear) {
      this.overviewToTabService.setYearSelection(paramYear);
      this.setFoundHearingYearWhenQueryParamIsProvided(timetableHearingYears, Number(paramYear));
    } else {
      this.overviewToTabService.setYearSelection(this.YEAR_DROPDOWN_OPTIONS[0]);
      this.overviewToTabService.setTimetableHearingYear(timetableHearingYears[0]);
    }
  }

  private setFoundHearingYearWhenQueryParamIsProvided(
    timetableHearingYears: TimetableHearingYear[],
    paramYear: number
  ): void {
    const matchedHearingYear = timetableHearingYears.find((value) => value.timetableYear === paramYear);

    if (matchedHearingYear) {
      this.overviewToTabService.setTimetableHearingYear(matchedHearingYear);
      this.overviewToTabService.setYearSelection(paramYear);
    } else {
      this.overviewToTabService.setYearSelection(this.YEAR_DROPDOWN_OPTIONS[0]);
      this.overviewToTabService.setTimetableHearingYear(timetableHearingYears[0]);
      this.router
        .navigate([Pages.TTH.path, this.cantonShort().toLowerCase(), this.hearingStatus().toLowerCase()], {
          queryParamsHandling: 'merge',
        })
        .then();
    }
  }
}
