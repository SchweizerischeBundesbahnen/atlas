import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidiWorkflowOverviewComponent } from './lidi-workflow-overview.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { ContainerLineVersionSnapshot, LinesService, LineType } from '../../../api';
import { of } from 'rxjs';
import { TableComponent } from '../../../core/components/table/table.component';
import { MockAppTableSearchComponent } from '../../../app.testing.mocks';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';

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
    },
  ],
  totalCount: 1,
};

describe('LidiWorkflowOverviewComponent', () => {
  let component: LidiWorkflowOverviewComponent;
  let fixture: ComponentFixture<LidiWorkflowOverviewComponent>;

  const linesService = jasmine.createSpyObj('linesService', ['getLineVersionSnapshotModels']);
  linesService.getLineVersionSnapshotModels.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        TableComponent,
        LidiWorkflowOverviewComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(LidiWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(linesService.getLineVersionSnapshotModels).toHaveBeenCalled();

    expect(component.lineVersionSnapshots.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
