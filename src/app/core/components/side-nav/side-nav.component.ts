import { Component, EventEmitter, Output } from '@angular/core';
import { Page } from '../../model/page';
import { Pages } from '../../../pages/pages';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent {
  @Output() showSideNav = new EventEmitter<void>();

  pages: Page[] = Pages.pages;

  toggle(): void {
    this.showSideNav.emit();
  }
}
