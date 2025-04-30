import { ContainerLineVersionSnapshot } from 'src/app/api/model/containerLineVersionSnapshot';
import { LinesService, LineType, WorkflowStatus } from '../../../../api';
import { LidiWorkflowOverviewComponent } from './lidi-workflow-overview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../../app.testing.mocks';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

const versionContainer: ContainerLineVersionSnapshot = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      status: 'VALIDATED',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'SBB',
      workflowStatus: 'ADDED',
      lineType: LineType.Orderly,
      workflowId: 1,
      parentObjectId: 1,
      paymentType: 'INTERNATIONAL',
      shortNumber: 'asd',
      lineConcessionType: 'CANTONALLY_APPROVED_LINE',
      offerCategory: 'BAT',
    },
  ],
  totalCount: 1,
};

describe('LidiWorkflowOverviewComponent', () => {
  let component: LidiWorkflowOverviewComponent;
  let fixture: ComponentFixture<LidiWorkflowOverviewComponent>;

  let lineInternalServiceSpy: SpyObj<LineInternalService>;

  beforeEach(() => {
    lineInternalServiceSpy = jasmine.createSpyObj<LineInternalService>(
      'LineInternalServiceSpy',
      ['getLineVersionSnapshot']
    );
    (
      lineInternalServiceSpy.getLineVersionSnapshot as Spy<
        () => Observable<ContainerLineVersionSnapshot>
      >
    ).and.returnValue(of(versionContainer));

    TestBed.configureTestingModule({
      imports: [LidiWorkflowOverviewComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        { provide: LineInternalService, useValue: lineInternalServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideComponent(LidiWorkflowOverviewComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(LidiWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(
      lineInternalServiceSpy.getLineVersionSnapshot
    ).toHaveBeenCalledOnceWith(
      [],
      undefined,
      [WorkflowStatus.Added, WorkflowStatus.Approved, WorkflowStatus.Rejected],
      0,
      10,
      ['number,asc']
    );

    expect(component.lineVersionSnapshots.length).toEqual(1);
    expect(component.lineVersionSnapshots[0].slnid).toEqual('slnid');
    expect(component.totalCount$).toEqual(1);
  });
});
