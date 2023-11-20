import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reference-point',
  templateUrl: './reference-point.component.html',
  styleUrls: ['./reference-point.component.scss'],
})
export class ReferencePointComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
