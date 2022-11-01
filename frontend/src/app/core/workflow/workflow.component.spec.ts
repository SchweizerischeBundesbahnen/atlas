import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowComponent } from './workflow.component';
import { AppTestingModule } from '../../app.testing.module';
import { AuthService } from '../auth/auth.service';
import { Workflow } from '../../api';
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

const authServiceMock: Partial<AuthService> = {
  claims: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    roles: ['lidi-admin', 'lidi-writer'],
  },
};

describe('WorkflowComponent', () => {
  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule],
      declarations: [WorkflowComponent],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowComponent);
    fixture.componentInstance.workflow = {
      businessObjectId: 123,
      swissId: 'ch:slnid:0001',
      workflowType: WorkflowTypeEnum.Line,
      workflowComment: 'comment',
      description: 'description',
      client: {
        firstName: 'Marek',
        lastName: 'Hamsik',
        mail: '@b.ch',
        personFunction: 'function',
      },
    };
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
