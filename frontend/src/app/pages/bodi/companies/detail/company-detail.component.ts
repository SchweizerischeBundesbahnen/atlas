import { Component, Inject, OnInit } from '@angular/core';
import { Company } from '../../../../api';
import {
  MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA,
  MatLegacyDialogRef as MatDialogRef,
} from '@angular/material/legacy-dialog';

@Component({
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.scss'],
})
export class CompanyDetailComponent implements OnInit {
  company!: Company;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    readonly dialogRef: MatDialogRef<any>
  ) {}

  ngOnInit() {
    this.company = this.dialogData.companyDetail;
  }

  prependHttp(url: string | undefined) {
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
