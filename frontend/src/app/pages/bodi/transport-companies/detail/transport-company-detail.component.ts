import { Component, Inject, OnInit } from '@angular/core';
import { TransportCompany } from '../../../../api';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  templateUrl: './transport-company-detail.component.html',
  styleUrls: ['./transport-company-detail.component.scss'],
})
export class TransportCompanyDetailComponent implements OnInit {
  transportCompany!: TransportCompany;

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  ngOnInit() {
    this.transportCompany = this.dialogData.transportCompanyDetail;
  }
}
