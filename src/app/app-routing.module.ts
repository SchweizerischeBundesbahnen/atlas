import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthInsightsComponent } from './pages/auth-insights/auth-insights.component';
import { HomeComponent } from './pages/home/home.component';
import { AuthGuard } from './core/auth/auth-guard';
import { TimetableFieldNumberDetailComponent } from './pages/timetable-field-number-detail/timetable-field-number-detail.component';
import { TimetableFieldNumberDetailResolver } from './pages/timetable-field-number-detail/timetable-field-number-detail.resolver';
import { Pages } from './pages/pages';

// Use the AuthGuard in routes that should require a logged in user.
// Do NOT use it for the root route. If the user should always be logged in,
// see comment in the AppComponent constructor.
const routes: Routes = [
  {
    path: Pages.HOME.path,
    component: HomeComponent,
    data: {
      breadcrumb: Pages.HOME.title,
    },
  },
  {
    path: Pages.AUTH_INSIGHT.path,
    component: AuthInsightsComponent,
    canActivate: [AuthGuard],
    data: {
      breadcrumb: Pages.AUTH_INSIGHT.title,
    },
  },
  {
    path: Pages.TTFN_DETAILS.path,
    component: TimetableFieldNumberDetailComponent,
    resolve: {
      timetableFieldNumberDetail: TimetableFieldNumberDetailResolver,
    },
    data: {
      breadcrumb: Pages.TTFN_DETAILS.title,
    },
    runGuardsAndResolvers: 'always',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
