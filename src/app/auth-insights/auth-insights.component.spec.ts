import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthInsightsComponent } from './auth-insights.component';
import { AuthService } from '../core/auth.service';
import { SbbAngularLibraryModule } from '../shared/sbb-angular-library.module';

const authServiceMock: Partial<AuthService> = {};

describe('AuthInsightsComponent', () => {
  let component: AuthInsightsComponent;
  let fixture: ComponentFixture<AuthInsightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbbAngularLibraryModule],
      declarations: [AuthInsightsComponent],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthInsightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
