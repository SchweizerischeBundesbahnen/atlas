import { Component } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';
import { PageService } from '../../core/pages/page.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  readonly enabledPages: Observable<Page[]>;

  constructor(private readonly pageService: PageService) {
    this.enabledPages = pageService.enabledPages.pipe(
      map((pages) => pages.filter((page) => page !== Pages.HOME)),
    );
  }
}
