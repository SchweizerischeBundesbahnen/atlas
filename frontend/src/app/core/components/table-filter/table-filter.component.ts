import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl, FormGroup } from '@angular/forms';
import moment from 'moment';
import {
  FilterType,
  TableFilterConfig,
  TableFilterDateSelect,
  TableFilterMultiSelect,
  TableFilterSearchSelect,
} from './table-filter-config';
import { MatSelectChange } from '@angular/material/select';
import { TypeGuard } from './filter-type-guard.pipe';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
})
export class TableFilterComponent<TFilterConfig> {
  /*@Input() additionalFieldTemplate!: TemplateRef<any>;
  @Input() displayStatus = true;
  @Input() displayValidOn = true;
  @Input() displayBusinessOrganisationSearch = true;
  @Input() searchTextColumnStyle = 'col-4';
  */

  // @Input() searchStatusType: SearchStatusType = 'DEFAULT_STATUS';

  @Input() filterConfigurations: TableFilterConfig<TFilterConfig>[] = [];

  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  // @ViewChild('validOnInput') validOnInput!: ElementRef;

  /*@ViewChild(BusinessOrganisationSelectComponent)
  businessOrganisationSelectComponent!: BusinessOrganisationSelectComponent;*/

  isDateSelect: TypeGuard<TableFilterConfig<TFilterConfig>, TableFilterDateSelect> = (
    filterType: TableFilterConfig<TFilterConfig>
  ): filterType is TableFilterDateSelect => filterType.filterType === FilterType.VALID_ON_SELECT;

  isSearchSelect: TypeGuard<
    TableFilterConfig<TFilterConfig>,
    TableFilterSearchSelect<TFilterConfig>
  > = (
    filterType: TableFilterConfig<TFilterConfig>
  ): filterType is TableFilterSearchSelect<TFilterConfig> =>
    filterType.filterType === FilterType.SEARCH_SELECT;

  isMultiSelect: TypeGuard<
    TableFilterConfig<TFilterConfig>,
    TableFilterMultiSelect<TFilterConfig>
  > = (
    filterType: TableFilterConfig<TFilterConfig>
  ): filterType is TableFilterMultiSelect<TFilterConfig> =>
    filterType.filterType === FilterType.MULTI_SELECT;

  boSearchForm = new FormGroup<BusinessOrganisationSearch>({
    businessOrganisation: new FormControl(),
  });

  // STATUS_OPTIONS: Array<Status | WorkflowStatus> = Object.values(Status);
  // readonly LINE_TYPES: LineType[] = Object.values(LineType);

  // STATUS_TYPES_PREFIX_LABEL = 'COMMON.STATUS_TYPES.';
  searchStrings: string[] = [];
  // searchDate?: Date;
  // activeStatuses: statusChoice = [];

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  addSearch(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (this.searchStrings.indexOf(value) !== -1) {
      event.chipInput!.clear();
      return;
    }
    if (value) {
      this.searchStrings.push(value);
    }
    // Clear the input value
    event.chipInput!.clear();
    this.emitSearch();
  }

  removeSearch(search: string): void {
    const index = this.searchStrings.indexOf(search);
    if (index >= 0) {
      this.searchStrings.splice(index, 1);
    }
    this.emitSearch();
  }

  onDateChanged(event: MatDatepickerInputEvent<Date>, filterIndex: number): void {
    if ((this.filterConfigurations[filterIndex] as TableFilterDateSelect).formControl.invalid)
      return;

    (this.filterConfigurations[filterIndex] as TableFilterDateSelect).activeSearch = event.value
      ? moment(event.value).toDate()
      : undefined;

    //this.searchDate = event.value ? moment(event.value).toDate() : undefined;
    this.emitSearch();
  }

  multiSelectChanged(changeEvent: MatSelectChange, filterIndex: number) {
    (this.filterConfigurations[filterIndex] as TableFilterMultiSelect<TFilterConfig>).activeSearch =
      changeEvent.value as TFilterConfig[];

    //this.boSearchForm.patchValue($event, { emitEvent: false });
    this.emitSearch();
  }

  multiSelectSearchChanged(changeEvent: unknown, filterIndex: number) {
    (
      this.filterConfigurations[filterIndex] as TableFilterSearchSelect<TFilterConfig>
    ).activeSearch = changeEvent as TFilterConfig;
    this.emitSearch();
  }

  // TODO: check
  // ngOnInit(): void {
  //   if (this.searchStatusType === 'WORKFLOW_STATUS') {
  //     this.STATUS_OPTIONS = Object.values(WorkflowStatus).filter(
  //       (value) =>
  //         value === WorkflowStatus.Added ||
  //         value === WorkflowStatus.Approved ||
  //         value === WorkflowStatus.Rejected
  //     );
  //     this.STATUS_TYPES_PREFIX_LABEL = 'WORKFLOW.STATUS.';
  //   }
  // }

  private emitSearch(): void {
    // this.activeSearch.searchCriteria = this.searchStrings;
    // this.activeSearch.validOn = this.searchDate;
    // this.activeSearch.statusChoices = this.activeStatuses;
    // this.activeSearch.boChoice = this.boSearchForm.get('businessOrganisation')?.value;
    // TODO: searchFieldWithGridStyling
    this.searchEvent.emit();
  }
}

interface BusinessOrganisationSearch {
  businessOrganisation: FormControl<string | undefined>;
}
