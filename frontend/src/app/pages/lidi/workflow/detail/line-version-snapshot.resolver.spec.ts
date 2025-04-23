import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import {
  lineVersionSnapshotResolver,
  LineVersionSnapshotResolver,
} from './line-version-snapshot.resolver';
import {
  LineType,
  LineVersionSnapshot,
  PaymentType,
  WorkflowStatus,
} from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { LineInternalService } from '../../../../api/service/line-internal.service';

const version: LineVersionSnapshot = {
  lineType: LineType.Operational,
  parentObjectId: 0,
  paymentType: PaymentType.International,
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

const routerStateSnapshot = jasmine.createSpyObj('RouterStateSnapshot', ['']);

describe('LineVersionSnapshotResolver', () => {
  const lineInternalService = jasmine.createSpyObj('lineInternalService', [
    'getLineVersionSnapshotById',
  ]);
  lineInternalService.getLineVersionSnapshotById.and.returnValue(of(version));

  let resolver: LineVersionSnapshotResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        LineVersionSnapshotResolver,
        { provide: LineInternalService, useValue: lineInternalService },
      ],
    });
    resolver = TestBed.inject(LineVersionSnapshotResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get snapshot from service to display', () => {
    const mockRoute = {
      params: { id: '1234' },
    } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      lineVersionSnapshotResolver(mockRoute, routerStateSnapshot)
    ) as Observable<LineVersionSnapshot>;

    result.subscribe((snapshot) => {
      expect(snapshot.id).toBe(1234);
    });
    expect(lineInternalService.getLineVersionSnapshotById).toHaveBeenCalled();
  });
});
