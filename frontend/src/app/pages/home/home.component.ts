import { Component } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';
import {PageService} from "../../core/auth/page.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {

  constructor(private pageService: PageService) {
  }

  get enabledPages(): Page[] {
    return this.pageService.enabledPages.filter((page) => page !== Pages.HOME);
  }
}
