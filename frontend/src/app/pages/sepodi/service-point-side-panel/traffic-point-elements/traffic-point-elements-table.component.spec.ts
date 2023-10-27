import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsTableComponent } from './traffic-point-elements-table.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { AppTestingModule } from '../../../../app.testing.module';

const authService: Partial<AuthService> = {};

describe('TrafficPointElementsTableComponent', () => {
  let component: TrafficPointElementsTableComponent;
  let fixture: ComponentFixture<TrafficPointElementsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TrafficPointElementsTableComponent],
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
