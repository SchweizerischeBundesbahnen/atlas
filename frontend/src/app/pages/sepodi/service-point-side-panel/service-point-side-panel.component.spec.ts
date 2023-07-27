import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointSidePanelComponent } from './service-point-side-panel.component';
import { AuthService } from '../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('SepodiOverviewComponent', () => {
  let component: ServicePointSidePanelComponent;
  let fixture: ComponentFixture<ServicePointSidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServicePointSidePanelComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointSidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
