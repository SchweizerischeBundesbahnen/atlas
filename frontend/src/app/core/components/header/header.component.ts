import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {ActivatedRouteSnapshot, NavigationEnd, Router} from '@angular/router';
import {filter, map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {NON_PROD_STAGES, Stages} from '../../constants/stages';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  version: string = environment.appVersion;
  showLabel = true;
  environmentLabel: string = environment.label;
  environmentReleaseNotesUrl: string = environment.atlasReleaseNotes;
  headerTitle$: Observable<string>;

  isItWednesday = false;

  constructor(router: Router) {
    this.headerTitle$ = router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.getHeaderTitleForCurrentRoute(router.routerState.snapshot.root))
    );
  }

  ngOnInit() {
    this.isItWednesday = new Date().getDay() === 3;
    this.showLabel = NON_PROD_STAGES.includes(this.environmentLabel);
  }

  private getHeaderTitleForCurrentRoute(node: ActivatedRouteSnapshot): string {
    if (!node.firstChild?.data.headerTitle) {
      return node.data.headerTitle;
    }
    return this.getHeaderTitleForCurrentRoute(node.firstChild);
  }

  getEnvLabelClass() {
    return {
      badge: true,
      'text-wrap': true,
      'ms-5': true,
      'bg-primary': this.environmentLabel === Stages.DEV,
      'bg-secondary': this.environmentLabel === Stages.TEST,
      'bg-warning': this.environmentLabel === Stages.INT,
    };
  }
}
