import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { firstValueFrom, Observable, of } from 'rxjs';
import { lineVersionSnapshotResolver, LineVersionSnapshotResolver } from './line-version-snapshot.resolver';
import { LineType, LineVersionSnapshot, WorkflowStatus } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const version: LineVersionSnapshot = {
  lineType: LineType.Operational,
  parentObjectId: 0,
  workflowId: 0,
  workflowStatus: WorkflowStatus.Approved,
  id: 1234,
  description: 'description',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  number: '1.1',
  businessOrganisation: 'sbb',
  offerCategory: 'BAT',
  lineConcessionType: 'CANTONALLY_APPROVED_LINE',
  shortNumber: 'short',
};

describe('LineVersionSnapshotResolver', () => {
  let resolver: LineVersionSnapshotResolver;
  let lineInternalService: Mocked<Pick<LineInternalService, 'getLineVersionSnapshotById'>>;

  beforeEach(() => {
    lineInternalService = {
      getLineVersionSnapshotById: vi.fn(),
    };
    lineInternalService.getLineVersionSnapshotById.mockReturnValue(of(version));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [LineVersionSnapshotResolver, { provide: LineInternalService, useValue: lineInternalService }],
    });

    resolver = TestBed.inject(LineVersionSnapshotResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get snapshot from service to display', async () => {
    const mockRoute = new ActivatedRouteSnapshot();
    mockRoute.params = { id: '1234' };

    const mockRouterState = {} as RouterStateSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      lineVersionSnapshotResolver(mockRoute, mockRouterState)
    ) as Observable<LineVersionSnapshot>;

    const snapshot = await firstValueFrom(result);
    expect(snapshot.id).toBe(1234);
    expect(lineInternalService.getLineVersionSnapshotById).toHaveBeenCalled();
  });
});
