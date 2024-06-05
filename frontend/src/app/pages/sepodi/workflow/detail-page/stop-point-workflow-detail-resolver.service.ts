import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, ResolveFn, Router} from '@angular/router';
import {catchError, mergeMap, Observable, of} from 'rxjs';
import {ReadServicePointVersion, ReadStopPointWorkflow, ServicePointsService, StopPointWorkflowService} from "../../../../api";
import {Pages} from "../../../pages";
import {map} from "rxjs/operators";

export interface StopPointWorkflowDetailData {
  workflow: ReadStopPointWorkflow,
  version: ReadServicePointVersion
}

@Injectable({providedIn: 'root'})
export class StopPointWorkflowDetailResolver {

  constructor(
    private readonly workflowService: StopPointWorkflowService,
    private readonly servicePointService: ServicePointsService,
    private readonly router: Router,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot): Observable<StopPointWorkflowDetailData | undefined> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    return this.workflowService.getStopPointWorkflow(idParameter)
      .pipe(catchError(() => {
          this.router.navigate([Pages.SEPODI.path], {
            state: {notDismissSnackBar: true},
          }).then()
          return of(undefined);
        }),
        mergeMap(workflow => {
          if (workflow) {
            return this.servicePointService.getServicePointVersion(workflow.versionId).pipe(map(servicePointVersion => {
              return {
                workflow: workflow,
                version: servicePointVersion
              };
            }));
          }
          return of();
        })
      );
  }
}

export const stopPointWorkflowDetailResolver: ResolveFn<StopPointWorkflowDetailData | undefined> = (
  route: ActivatedRouteSnapshot,
) => inject(StopPointWorkflowDetailResolver).resolve(route);
