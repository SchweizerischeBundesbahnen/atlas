import { Component, OnInit } from '@angular/core';
import { ClientCredential } from '../../../../api';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-client-credential-administration',
    templateUrl: './user-administration-client-detail.component.html',
    standalone: false
})
export class UserAdministrationClientDetailComponent implements OnInit {
  constructor(private activatedRoute: ActivatedRoute) {}

  clientCredential: ClientCredential = {};

  ngOnInit(): void {
    this.clientCredential = this.activatedRoute.snapshot.data.clientCredential;
  }
}
