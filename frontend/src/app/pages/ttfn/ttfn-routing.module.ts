import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { TimetableFieldNumberOverviewComponent } from './overview/timetable-field-number-overview.component';
import { TimetableFieldNumberDetailComponent } from './detail/timetable-field-number-detail.component';
import { timetableFieldNumberResolver } from './detail/timetable-field-number-detail.resolver';
import { Pages } from '../pages';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

const routes: Routes = [
  {
    path: Pages.TTFN_DETAIL.path,
    component: TimetableFieldNumberDetailComponent,
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      timetableFieldNumberDetail: timetableFieldNumberResolver,
    },
    runGuardsAndResolvers: 'always',
  },
  {
    path: '',
    component: TimetableFieldNumberOverviewComponent,
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TtfnRoutingModule {}
