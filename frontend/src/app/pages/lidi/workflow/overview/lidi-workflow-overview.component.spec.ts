import { ContainerLineVersionSnapshot } from 'src/app/api/model/containerLineVersionSnapshot';
import { LinesService, LineType, WorkflowStatus } from '../../../../api';
import { LidiWorkflowOverviewComponent } from './lidi-workflow-overview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../../app.testing.mocks';
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
      colorBackCmyk: '0.0.0.0',
      colorFontCmyk: '0.0.0.0',
      colorBackRgb: '#00000',
      colorFontRgb: '#00000',
    },
  ],
  totalCount: 1,
};

describe('LidiWorkflowOverviewComponent', () => {
  let component: LidiWorkflowOverviewComponent;
  let fixture: ComponentFixture<LidiWorkflowOverviewComponent>;

  let linesServiceSpy: SpyObj<LinesService>;

  beforeEach(() => {
    linesServiceSpy = jasmine.createSpyObj<LinesService>('LinesServiceSpy', [
      'getLineVersionSnapshot',
    ]);
    (
      linesServiceSpy.getLineVersionSnapshot as Spy<() => Observable<ContainerLineVersionSnapshot>>
    ).and.returnValue(of(versionContainer));

    TestBed.configureTestingModule({
      declarations: [LidiWorkflowOverviewComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesServiceSpy }, TranslatePipe],
    }).compileComponents();

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

    expect(linesServiceSpy.getLineVersionSnapshot).toHaveBeenCalledOnceWith(
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
