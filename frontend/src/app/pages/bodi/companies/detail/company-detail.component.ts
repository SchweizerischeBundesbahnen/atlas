import { Component, Inject, OnInit } from '@angular/core';
import { Company } from '../../../../api';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.scss'],
})
export class CompanyDetailComponent implements OnInit {
  company!: Company;

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  ngOnInit() {
    this.company = this.dialogData.companyDetail;
  }

  prependHttp(url: string | undefined) {
    if (!url) {
      return url;
    }
    if (url.startsWith('http')) {
      return url;
    }
    return 'http://' + url;
  }
}
