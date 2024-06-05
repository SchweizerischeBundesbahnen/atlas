import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, ResolveFn, Router} from '@angular/router';
import {catchError, Observable, of} from 'rxjs';
import {ReadStopPointWorkflow, StopPointWorkflowService} from "../../../../api";
import {Pages} from "../../../pages";

@Injectable({providedIn: 'root'})
export class StopPointWorkflowDetailResolver {

  constructor(
    private readonly workflowService: StopPointWorkflowService,
    private readonly router: Router,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot): Observable<ReadStopPointWorkflow | undefined> {
    const idParameter = parseInt(route.paramMap.get('id') || '0');
    console.log("resolving")
    return this.workflowService.getStopPointWorkflow(idParameter).pipe(
      catchError(() => {
        this.router.navigate([Pages.SEPODI.path], {
          state: {notDismissSnackBar: true},
        }).then()
        return of(undefined);
      }),
    );
  }
}

export const stopPointWorkflowDetailResolver: ResolveFn<ReadStopPointWorkflow | undefined> = (
  route: ActivatedRouteSnapshot,
) => inject(StopPointWorkflowDetailResolver).resolve(route);
