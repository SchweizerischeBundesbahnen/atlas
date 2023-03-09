import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { BusinessOrganisation, LineType, PaymentType } from '../../../../../api';
import { BusinessOrganisationSearchService } from '../../../../../core/service/business-organisation-search.service';
import { EMPTY, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'line-detail-form',
  templateUrl: './line-detail-form.component.html',
  styleUrls: ['./line-detail-form.component.scss'],
})
export class LineDetailFormComponent implements OnInit {
  @Input() form!: FormGroup;
  @Input() newRecord = false;
  @Input() boSboidRestriction: string[] = [];
  TYPE_OPTIONS = Object.values(LineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);

  selectedBusinessOrganisation$: Observable<BusinessOrganisation> = EMPTY;

  constructor(
    private readonly businessOrganisationSearchService: BusinessOrganisationSearchService
  ) {}

  ngOnInit() {
    const sboid: string = this.form.get('businessOrganisation')?.value;
    this.selectedBusinessOrganisation$ = this.businessOrganisationSearchService
      .searchByString(sboid)
      .pipe(map((foundBusinessOrganisations) => foundBusinessOrganisations[0]));
  }

  onBusinessOrganisationChange(businessOrganisation: BusinessOrganisation | null) {
    if (businessOrganisation) {
      this.form.get('businessOrganisation')?.setValue(businessOrganisation.sboid);
      this.selectedBusinessOrganisation$ = of(businessOrganisation);
    } else {
      this.form.get('businessOrganisation')?.setValue(null);
      this.selectedBusinessOrganisation$ = of();
    }
  }
}
