import { computed, Injectable, signal } from '@angular/core';
import { Cantons } from '../../../../core/cantons/Cantons';
import { HearingStatus, TimetableHearingYear } from '../../../../api';
import moment from 'moment';

@Injectable({
  providedIn: 'root',
})
export class OverviewToTabShareDataService {
  private readonly _hearingStatus = signal<HearingStatus>(HearingStatus.Active);
  private readonly _cantonShort = signal<string>(Cantons.swiss.path);
  private readonly _isPlannedTimetableHearingYearFound = signal<boolean>(false);
  private readonly _timetableYear = signal<TimetableHearingYear>({
    timetableYear: moment().toDate().getFullYear() + 1,
    hearingFrom: moment().toDate(),
    hearingTo: moment().toDate(),
  });
  private readonly _isYearLoading = signal<boolean>(false);
  private readonly _isTimetableHearingYearFound = signal<boolean>(false);
  private readonly _yearSelection = signal<number>(0);

  readonly cantonShort = this._cantonShort.asReadonly();

  readonly hearingStatus = this._hearingStatus.asReadonly();

  readonly timetableYear = this._timetableYear.asReadonly();

  readonly isPlannedTimetableHearingYearFound = this._isPlannedTimetableHearingYearFound.asReadonly();

  readonly isYearLoading = this._isYearLoading.asReadonly();

  readonly isTimetableHearingYearFound = this._isTimetableHearingYearFound.asReadonly();

  readonly yearSelection = this._yearSelection.asReadonly();

  readonly isHearingYearActive = computed(() => this.hearingStatus() === HearingStatus.Active);

  readonly isHearingYearArchived = computed(() => this.hearingStatus() === HearingStatus.Archived);

  readonly isHearingYearPlanned = computed(() => this.hearingStatus() === HearingStatus.Planned);

  readonly isSwissCanton = computed(() => this.cantonShort().toLowerCase() === Cantons.swiss.short.toLowerCase());

  setHearingStatus(hearingStatus: HearingStatus) {
    this._hearingStatus.set(hearingStatus);
  }

  setCantonShort(cantonShort: string): void {
    this._cantonShort.set(cantonShort);
  }

  setPlannedTimetableHearingYearFound(isFound: boolean): void {
    this._isPlannedTimetableHearingYearFound.set(isFound);
  }

  setTimetableHearingYear(year: TimetableHearingYear): void {
    this._timetableYear.set(year);
  }

  setTimetableHearingYearFound(isFound: boolean): void {
    this._isTimetableHearingYearFound.set(isFound);
  }

  setTimetableHearingYearLoading(loading: boolean): void {
    this._isYearLoading.set(loading);
  }

  setYearSelection(year: number): void {
    this._yearSelection.set(year);
  }
}
