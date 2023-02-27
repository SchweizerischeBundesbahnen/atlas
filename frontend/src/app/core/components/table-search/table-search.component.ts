import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { MatChipInputEvent } from '@angular/material/chips';
import { statusChoice, TableSearch } from './table-search';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { DATE_PATTERN, MAX_DATE, MIN_DATE } from '../../date/date.service';
import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Status, WorkflowStatus } from '../../../api';
import moment from 'moment';
import { ValidationService } from '../../validation/validation.service';
import { BusinessOrganisationSelectComponent } from '../../form-components/bo-select/business-organisation-select.component';
import { BaseTableSearch, SearchStatusType } from './base-table-search';

@Component({
  selector: 'app-table-search',
  templateUrl: './table-search.component.html',
  styleUrls: ['./table-search.component.scss'],
})
export class TableSearchComponent implements OnInit {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @Input() additionalFieldTemplate!: TemplateRef<any>;
  @Input() displayStatus = true;
  @Input() displayValidOn = true;
  @Input() displayBusinessOrganisationSearch = true;
  @Input() searchTextColumnStyle = 'col-4';
  @Input() searchStatusType: SearchStatusType = 'DEFAULT_STATUS';

  @Output() searchEvent: EventEmitter<BaseTableSearch> = new EventEmitter<BaseTableSearch>();
  @ViewChild('validOnInput') validOnInput!: ElementRef;

  @ViewChild(BusinessOrganisationSelectComponent)
  businessOrganisationSelectComponent!: BusinessOrganisationSelectComponent;
  boSearchForm = new FormGroup<BusinessOrganisationSearch>({
    businessOrganisation: new FormControl(),
  });

  STATUS_OPTIONS: Array<Status | WorkflowStatus> = Object.values(Status);
  STATUS_TYPES_PREFIX_LABEL = 'COMMON.STATUS_TYPES.';
  searchStrings: string[] = [];
  searchDate?: Date;
  activeStatuses: statusChoice = [];
  activeSearch: TableSearch = {
    searchCriteria: this.searchStrings,
    validOn: this.searchDate,
    statusChoices: this.activeStatuses,
  };

  dateControl = new FormControl();

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  constructor(private readonly validationService: ValidationService) {}

  getDateControlValidation() {
    return this.validationService.getValidation(this.dateControl.errors);
  }

  getMinOrMaxDateDisplay(validationErrors: ValidationErrors) {
    if (validationErrors.min) return validationErrors.min.format(DATE_PATTERN);
    if (validationErrors.max) return validationErrors.max.format(DATE_PATTERN);
  }

  statusSelectionChange(): void {
    this.emitSearch();
  }

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

  onDateChanged(event: MatDatepickerInputEvent<Date>): void {
    if (this.dateControl.invalid) return;
    this.searchDate = event.value ? moment(event.value).toDate() : undefined;
    this.emitSearch();
  }

  businessOrganisationChanged($event: any) {
    this.boSearchForm.patchValue($event, { emitEvent: false });
    this.emitSearch();
  }

  restoreBusinessOrganisation(sboid: string) {
    if (this.businessOrganisationSelectComponent) {
      this.boSearchForm.patchValue({ businessOrganisation: sboid }, { emitEvent: false });
      this.businessOrganisationSelectComponent.searchBusinessOrganisation(sboid);
    }
  }

  ngOnInit(): void {
    if (this.searchStatusType === 'WORKFLOW_STATUS') {
      this.STATUS_OPTIONS = Object.values(WorkflowStatus).filter(
        (value) =>
          value === WorkflowStatus.Added ||
          value === WorkflowStatus.Approved ||
          value === WorkflowStatus.Rejected
      );
      this.STATUS_TYPES_PREFIX_LABEL = 'WORKFLOW.STATUS.';
    }
  }

  private emitSearch(): void {
    this.activeSearch.searchCriteria = this.searchStrings;
    this.activeSearch.validOn = this.searchDate;
    this.activeSearch.statusChoices = this.activeStatuses;
    this.activeSearch.boChoice = this.boSearchForm.get('businessOrganisation')?.value;
    this.searchEvent.emit(this.activeSearch);
  }
}

interface BusinessOrganisationSearch {
  businessOrganisation: FormControl<string | undefined>;
}
