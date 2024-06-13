import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StopPointWorkflowOverviewComponent} from './stop-point-workflow-overview.component';
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {TableComponent} from "../../../../core/components/table/table.component";
import {TableFilterComponent} from "../../../../core/components/table-filter/table-filter.component";

describe('StopPointWorkflowOverviewComponent', () => {
  let component: StopPointWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointWorkflowOverviewComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowOverviewComponent,
        TableComponent,
        TableFilterComponent,
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
