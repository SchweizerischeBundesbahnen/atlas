import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthInsightsComponent } from './auth-insights/auth-insights.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './core/auth-guard';

// Use the AuthGuard in routes that should require a logged in user.
// Do NOT use it for the root route. If the user should always be logged in,
// see comment in the AppComponent constructor.
const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth-insights', component: AuthInsightsComponent, canActivate: [AuthGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
