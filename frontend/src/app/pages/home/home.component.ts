import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Pages } from '../pages';
import { Page } from '../../core/model/page';
import { PageService } from '../../core/pages/page.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, AsyncPipe, TranslatePipe],
})
export class HomeComponent {
  private readonly pageService = inject(PageService);
  readonly enabledPages: Observable<Page[]> = this.pageService.enabledPages.pipe(
    map((pages) => pages.filter((page) => page !== Pages.HOME))
  );
}
