import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointSidePanelComponent } from './service-point-side-panel.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { FormatServicePointNumber } from '../number-pipe/service-point-number.pipe';
import { of } from 'rxjs';
import { BERN_WYLEREGG } from '../service-point-test-data';
import { AuthService } from '../../../core/auth/auth.service';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';

const authService: Partial<AuthService> = {};

describe('ServicePointSidePanelComponent', () => {
  let component: ServicePointSidePanelComponent;
  let fixture: ComponentFixture<ServicePointSidePanelComponent>;

  const activatedRouteMock = { data: of({ servicePoint: [BERN_WYLEREGG] }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ServicePointSidePanelComponent,
        DisplayDatePipe,
        FormatServicePointNumber,
        MockAtlasButtonComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointSidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
