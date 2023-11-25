import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointDetailComponent } from './stop-point-detail.component';
import { of } from 'rxjs';
import { STOP_POINT } from '../stop-point-test-data';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';

const authService: Partial<AuthService> = {};
describe('StopPointDetailComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;

  const activatedRouteMock = {
    parent: { data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }) },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StopPointDetailComponent, MockAtlasButtonComponent, SwitchVersionComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        TranslatePipe,
      ],
    });
    fixture = TestBed.createComponent(StopPointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
