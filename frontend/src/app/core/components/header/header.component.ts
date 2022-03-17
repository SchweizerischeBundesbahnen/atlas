import { Component } from '@angular/core';
import packageJson from '../../../../../package.json';
import { environment } from '../../../../environments/environment';
import { NavigationEnd, Router, ActivatedRouteSnapshot } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent {
  private readonly DEV: string = 'dev';
  private readonly TEST: string = 'test';
  private readonly INT: string = 'int';
  private readonly STAGES_WITH_LABEL = [this.DEV, this.TEST, this.INT];

  version: string = packageJson.version;
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
    return this.STAGES_WITH_LABEL.includes(this.environmentLabel);
  }

  getEnvLabelClass() {
    return {
      badge: true,
      'text-wrap': true,
      'ms-2': true,
      'bg-primary': this.environmentLabel === this.DEV,
      'bg-secondary': this.environmentLabel === this.TEST,
      'bg-warning': this.environmentLabel === this.INT,
    };
  }
}
