import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthInsightsComponent } from './auth-insights.component';
import { AuthService } from '../../core/auth/auth.service';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';

const authServiceMock: Partial<AuthService> = {};

describe('AuthInsightsComponent', () => {
  let component: AuthInsightsComponent;
  let fixture: ComponentFixture<AuthInsightsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AuthInsightsComponent],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
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
