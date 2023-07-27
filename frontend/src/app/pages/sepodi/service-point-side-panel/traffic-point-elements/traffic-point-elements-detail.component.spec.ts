import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficPointElementsDetailComponent } from './traffic-point-elements-detail.component';
import { AuthService } from '../../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('TrafficPointElementsDetailComponent', () => {
  let component: TrafficPointElementsDetailComponent;
  let fixture: ComponentFixture<TrafficPointElementsDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TrafficPointElementsDetailComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(TrafficPointElementsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
