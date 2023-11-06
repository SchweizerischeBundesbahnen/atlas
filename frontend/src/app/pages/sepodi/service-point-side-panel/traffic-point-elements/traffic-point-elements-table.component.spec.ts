import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { AppTestingModule } from '../../../../app.testing.module';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TableFilterComponent } from '../../../../core/components/table-filter/table-filter.component';
import { LoadingSpinnerComponent } from '../../../../core/components/loading-spinner/loading-spinner.component';

const authService: Partial<AuthService> = {};

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TrafficPointElementsTableComponent,
        MockAtlasButtonComponent,
        TableComponent,
        TableFilterComponent,
        LoadingSpinnerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(TrafficPointElementsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
