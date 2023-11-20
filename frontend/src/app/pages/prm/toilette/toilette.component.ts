import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-toilette',
  templateUrl: './toilette.component.html',
  styleUrls: ['./toilette.component.scss'],
})
export class ToiletteComponent {
  constructor(private readonly router: Router) {}

  closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }
}
