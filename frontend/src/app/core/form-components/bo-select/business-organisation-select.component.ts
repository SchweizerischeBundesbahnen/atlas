import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Observable, of, Subscription } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { BusinessOrganisation, BusinessOrganisationsService } from '../../../api';
import { BusinessOrganisationLanguageService } from './business-organisation-language.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'bo-select',
  templateUrl: './business-organisation-select.component.html',
  styleUrls: ['./business-organisation-select.component.scss'],
})
export class BusinessOrganisationSelectComponent implements OnInit, OnDestroy {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;

  @Output() selectedBusinessOrganisationChanged = new EventEmitter();

  businessOrganisations: Observable<BusinessOrganisation[]> = of([]);
  private formSubscription!: Subscription;

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService
  ) {}

  ngOnInit(): void {
    const boControl = this.formGroup.get(this.controlName)!;
    this.formSubscription = boControl.valueChanges.subscribe((change) => {
      this.selectedBusinessOrganisationChanged.emit(change);
      this.searchBusinessOrganisation(change);
    });

    this.searchBusinessOrganisation(boControl.value as string);
  }

  searchBusinessOrganisation(searchString: string) {
    if (searchString) {
      this.businessOrganisations = this.businessOrganisationsService
        .getAllBusinessOrganisations([searchString])
        .pipe(map((value) => value.objects ?? []));
    }
  }

  ngOnDestroy() {
    this.formSubscription.unsubscribe();
  }

  readonly displaySettings = (item: BusinessOrganisation) => {
    return `${item.organisationNumber} - ${item[this.getCurrentLanguageAbbreviation()]} - ${
      item[this.getCurrentLanguageDescription()]
    } - ${item.said}`;
  };

  private getCurrentLanguageAbbreviation() {
    return this.businessOrganisationLanguageService.getCurrentLanguageAbbreviation();
  }

  private getCurrentLanguageDescription() {
    return this.businessOrganisationLanguageService.getCurrentLanguageDescription();
  }
}
