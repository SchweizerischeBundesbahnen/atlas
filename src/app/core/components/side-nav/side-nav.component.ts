import { Component, EventEmitter, Output } from '@angular/core';
import { Page } from '../../../model/page';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
})
export class SideNavComponent {
  @Output() showSideNav = new EventEmitter<void>();

  pages: Page[] = [
    {
      title: 'PAGES.HOME',
      link: '',
      icon: 'bi-house-fill',
    },
    {
      title: 'PAGES.AUTH_INSIGHT',
      link: '/auth-insights',
      icon: 'bi-key-fill',
    },
  ];

  toggle(): void {
    this.showSideNav.emit();
  }
}
