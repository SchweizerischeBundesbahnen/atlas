import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { ReadServicePointVersion } from '../../../../api';
import { inject, Injectable } from '@angular/core';
import { catchError, mergeMap, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { map } from 'rxjs/operators';
import { StopPointTerminationWorkflowService } from '../../../../api/service/workflow/stop-point-termination-workflow.service';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';

export interface StopPointTerminationWorkflowDetailData {
  workflow: TerminationStopPointWorkflowModel;
  servicePoint: ReadServicePointVersion[];
}

@Injectable({ providedIn: 'root' })
export class StopPointTerminationWorkflowResolver {
  constructor(
    private readonly workflowService: StopPointTerminationWorkflowService,
    private readonly servicePointService: ServicePointService,
    private readonly router: Router
  ) {}

  resolve(
    route: ActivatedRouteSnapshot
  ): Observable<StopPointTerminationWorkflowDetailData | undefined> {
    const idParameter = parseInt(route.paramMap.get('id') ?? '0');
    return this.workflowService.getTerminationById(idParameter).pipe(
      catchError(() => {
        this.router
          .navigate([Pages.TERMINATION_STOP_POINT_WORKFLOWS.path], {
            state: { notDismissSnackBar: true },
          })
          .then();
        return of(undefined);
      }),
      mergeMap((workflow) => {
        if (workflow) {
          return this.servicePointService
            .getServicePointVersionsBySloid(workflow.sloid)
            .pipe(
              map((servicePoint) => {
                return {
                  workflow: workflow,
                  servicePoint: servicePoint,
                };
              })
            );
        }
        return of();
      })
    );
  }
}

export const stopPointTerminationWorkflowResolver: ResolveFn<
  StopPointTerminationWorkflowDetailData | undefined
> = (route: ActivatedRouteSnapshot) =>
  inject(StopPointTerminationWorkflowResolver).resolve(route);
