import { Component, Inject, OnInit } from '@angular/core';
import { Company } from '../../../../api';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { CompanyFormGroup } from './company-form-group';

@Component({
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.scss'],
})
export class CompanyDetailComponent implements OnInit {
  company!: Company;

  form!: FormGroup<CompanyFormGroup>;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    readonly dialogRef: MatDialogRef<any>
  ) {}

  ngOnInit() {
    this.company = this.dialogData.companyDetail;
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
    return 'http://' + trimmedUrl;
  }
}
