import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { ActivatedRouteSnapshot, NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { merge, Observable, of } from 'rxjs';
import { NON_PROD_STAGES, Stages } from '../../constants/stages';
import { MatToolbar } from '@angular/material/toolbar';
import { AsyncPipe, NgClass, NgOptimizedImage } from '@angular/common';
import { LanguageSwitcherComponent } from '../language-switcher/language-switcher.component';
import { UserComponent } from '../user/user.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MaintenanceIconComponent } from './maintenance-icon/maintenance-icon.component';
import { InfoIconComponent } from '@atlas/form';

@Component({
  selector: 'atlas-header',
  templateUrl: './header.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./header.component.scss'],
  imports: [
    MatToolbar,
    RouterLink,
    InfoIconComponent,
    NgClass,
    LanguageSwitcherComponent,
    UserComponent,
    AsyncPipe,
    TranslatePipe,
    MaintenanceIconComponent,
    NgOptimizedImage,
  ],
  providers: [TranslatePipe],
})
export class HeaderComponent implements OnInit {
  version: string = environment.appVersion;
  showLabel = true;
  environmentLabel: string = environment.label;
  environmentReleaseNotesUrl: string = environment.atlasReleaseNotes;
  private readonly router = inject(Router);
  headerTitle$: Observable<string> = merge(
    of(this.getHeaderTitleForCurrentRoute(this.router.routerState.snapshot.root)),
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => this.getHeaderTitleForCurrentRoute(this.router.routerState.snapshot.root))
    )
  );

  isItWednesday = false;

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
      'bg-primary': this.environmentLabel === Stages.DEV,
      'bg-secondary': this.environmentLabel === Stages.TEST,
      'bg-warning': this.environmentLabel === Stages.INT,
    };
  }
}
