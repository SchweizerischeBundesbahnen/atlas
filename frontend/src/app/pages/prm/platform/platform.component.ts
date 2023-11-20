import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-platform',
  templateUrl: './platform.component.html',
  styleUrls: ['./platform.component.scss'],
})
export class PlatformComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
