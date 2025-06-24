import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { ReadServicePointVersion, ServicePointsService } from '../../../../api';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { inject, Injectable } from '@angular/core';
import { catchError, mergeMap, Observable, of } from 'rxjs';
import { Pages } from '../../../pages';
import { map } from 'rxjs/operators';
import { WorkflowService } from '../../../../api/service/workflow/workflow.service';

export interface StopPointTerminationWorkflowDetailData {
  workflow: TerminationStopPointAddWorkflow;
  servicePoint: ReadServicePointVersion[];
}

@Injectable({ providedIn: 'root' })
export class StopPointTerminationWorkflowResolver {
  constructor(
    private readonly workflowService: WorkflowService,
    private readonly servicePointService: ServicePointsService,
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
          .then((value) => {
            console.log(value);
          });
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
