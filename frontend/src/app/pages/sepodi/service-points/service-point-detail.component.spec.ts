import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointDetailComponent } from './service-point-detail.component';
import { AuthService } from '../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('SepodiOverviewComponent', () => {
  let component: ServicePointDetailComponent;
  let fixture: ComponentFixture<ServicePointDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServicePointDetailComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
