import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, Observable, of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import {
  StopPointWorkflowDetailData,
  stopPointWorkflowDetailResolver,
  StopPointWorkflowDetailResolver,
} from './stop-point-workflow-detail-resolver.service';
import { ReadStopPointWorkflow } from '../../../../api';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { AppTestingModule } from '../../../../app.testing.module';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';

describe('StopPointWorkflowDetailResolver', () => {
  const workflow: ReadStopPointWorkflow = {
    versionId: 1,
    sloid: 'ch:1:sloid:8000',
    workflowComment: 'No comment',
  };

  let stopPointWorkflowService: Mocked<Pick<StopPointWorkflowService, 'getStopPointWorkflow'>>;
  let servicePointsService: Mocked<Pick<ServicePointService, 'getServicePointVersionsBySloid'>>;

  let resolver: StopPointWorkflowDetailResolver;

  beforeEach(() => {
    stopPointWorkflowService = {
      getStopPointWorkflow: vi.fn().mockReturnValue(of(workflow)),
    };
    servicePointsService = {
      getServicePointVersionsBySloid: vi.fn().mockReturnValue(of([BERN_WYLEREGG])),
    };

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StopPointWorkflowDetailResolver,
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        { provide: ServicePointService, useValue: servicePointsService },
      ],
    });
    resolver = TestBed.inject(StopPointWorkflowDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get workflow with service point', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1000' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = TestBed.runInInjectionContext(() =>
      stopPointWorkflowDetailResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<StopPointWorkflowDetailData>;

    const workflowData = await firstValueFrom(resolvedVersion);
    expect(workflowData?.workflow.versionId).toBe(1);
    expect(workflowData?.servicePoint[0].designationOfficial).toBe('Bern, Wyleregg');
  });
});
