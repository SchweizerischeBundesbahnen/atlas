import { Component, Input, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { BusinessOrganisation, BusinessOrganisationsService } from '../../../api';
import { BusinessOrganisationLanguageService } from './business-organisation-language.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'bo-select',
  templateUrl: './business-organisation-select.component.html',
  styleUrls: ['./business-organisation-select.component.scss'],
})
export class BusinessOrganisationSelectComponent implements OnInit {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formGroup!: FormGroup;

  businessOrganisations: Observable<BusinessOrganisation[]> = of([]);

  constructor(
    private businessOrganisationsService: BusinessOrganisationsService,
    private businessOrganisationLanguageService: BusinessOrganisationLanguageService
  ) {}

  ngOnInit(): void {
    const currentSboid = this.formGroup.get(this.controlName)?.value;
    if (currentSboid)
      this.businessOrganisations = this.businessOrganisationsService
        .getAllBusinessOrganisations([currentSboid])
        .pipe(map((value) => value.objects ?? []));
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

  search(searchString: string) {
    this.businessOrganisations = this.businessOrganisationsService
      .getAllBusinessOrganisations([searchString])
      .pipe(map((value) => value.objects ?? []));
  }
}
