import { ContainerLineVersionSnapshot } from 'src/app/api/model/containerLineVersionSnapshot';
import { LinesService, LineType } from '../../../../api';
import { LidiWorkflowOverviewComponent } from './lidi-workflow-overview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../../app.testing.mocks';

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

  const linesService = jasmine.createSpyObj('linesService', ['getLineVersionSnapshot']);
  linesService.getLineVersionSnapshot.and.returnValue(of(versionContainer));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LidiWorkflowOverviewComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(LidiWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
