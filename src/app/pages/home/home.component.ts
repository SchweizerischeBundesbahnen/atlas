import { Component } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  pages: Page[] = Pages.pages.filter((page) => {
    return page !== Pages.HOME;
  });
}
