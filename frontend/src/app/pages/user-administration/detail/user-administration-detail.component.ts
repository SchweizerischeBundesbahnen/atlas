import { Component } from '@angular/core';

@Component({
  selector: 'app-user-administration-detail',
  templateUrl: './user-administration-detail.component.html',
  styleUrls: ['./user-administration-detail.component.scss'],
})
export class UserAdministrationDetailComponent {
  readonly applications = [
    { title: 'USER_ADMIN.APPLICATION.LIDI' },
    { title: 'USER_ADMIN.APPLICATION.TTFN' },
  ];
}
