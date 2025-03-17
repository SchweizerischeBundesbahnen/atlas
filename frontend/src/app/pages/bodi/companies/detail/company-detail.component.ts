import { Component, OnInit } from '@angular/core';
import { Company } from '../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { CompanyFormGroup } from './company-form-group';
import { ActivatedRoute } from '@angular/router';

@Component({
    templateUrl: './company-detail.component.html',
    styleUrls: ['./company-detail.component.scss'],
    standalone: false
})
export class CompanyDetailComponent implements OnInit {
  company!: Company;

  form!: FormGroup<CompanyFormGroup>;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.company = this.activatedRoute.snapshot.data.companyDetail;
    if (this.company) {
      this.form = new FormGroup<CompanyFormGroup>({
        uicCode: new FormControl({ value: this.company.uicCode, disabled: true }),
        countryCodeIso: new FormControl({ value: this.company.countryCodeIso, disabled: true }),
        shortName: new FormControl({ value: this.company.shortName, disabled: true }),
        name: new FormControl({ value: this.company.name, disabled: true }),
        url: new FormControl({ value: this.company.url, disabled: true }),
      });
    }
  }

  prependHttp(url: string | null | undefined) {
    if (!url) {
      return url;
    }
    const trimmedUrl = url.trim();
    if (trimmedUrl.startsWith('http')) {
      return trimmedUrl;
    }
    return 'https://' + trimmedUrl;
  }
}
