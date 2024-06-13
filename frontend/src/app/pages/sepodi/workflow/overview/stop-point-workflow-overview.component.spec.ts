import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StopPointWorkflowOverviewComponent} from './stop-point-workflow-overview.component';
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {MockTableComponent} from "../../../../app.testing.mocks";

describe('StopPointWorkflowOverviewComponent', () => {
  let component: StopPointWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointWorkflowOverviewComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowOverviewComponent,
        MockTableComponent
      ],
      imports: [AppTestingModule, FormModule],
    }).compileComponents().then();
    fixture = TestBed.createComponent(StopPointWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
