import { Component } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { NavigationEnd, Router, ActivatedRouteSnapshot } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { NON_PROD_STAGES, Stages } from '../../constants/stages';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  version: string = environment.appVersion;
  environmentLabel: string = environment.label;
  headerTitle$: Observable<string>;

  constructor(private readonly router: Router) {
    this.headerTitle$ = router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.getHeaderTitleForCurrentRoute(router.routerState.snapshot.root))
    );
  }

  private getHeaderTitleForCurrentRoute(node: ActivatedRouteSnapshot): string {
    if (!node.firstChild?.data.headerTitle) {
      return node.data.headerTitle;
    }
    return this.getHeaderTitleForCurrentRoute(node.firstChild);
  }

  showLabel() {
    return NON_PROD_STAGES.includes(this.environmentLabel);
  }

  getEnvLabelClass() {
    return {
      badge: true,
      'text-wrap': true,
      'ms-2': true,
      'bg-primary': this.environmentLabel === Stages.DEV,
      'bg-secondary': this.environmentLabel === Stages.TEST,
      'bg-warning': this.environmentLabel === Stages.INT,
    };
  }
}
