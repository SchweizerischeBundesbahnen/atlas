import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-connection',
  templateUrl: './connection.component.html',
  styleUrls: ['./connection.component.scss'],
})
export class ConnectionComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
