import { Component } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  get enabledPages(): Page[] {
    return Pages.enabledPages.filter((page) => page !== Pages.HOME);
  }
}
