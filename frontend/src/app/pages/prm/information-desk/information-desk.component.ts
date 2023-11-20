import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-information-desk',
  templateUrl: './information-desk.component.html',
  styleUrls: ['./information-desk.component.scss'],
})
export class InformationDeskComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
