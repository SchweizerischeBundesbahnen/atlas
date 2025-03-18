import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppComponent } from './app.component';
import { AuthService } from './core/auth/auth.service';
import { HeaderComponent } from './core/components/header/header.component';
import { UserComponent } from './core/components/user/user.component';
import { LanguageSwitcherComponent } from './core/components/language-switcher/language-switcher.component';
import { SideNavComponent } from './core/components/side-nav/side-nav.component';
import { LoadingSpinnerComponent } from './core/components/loading-spinner/loading-spinner.component';
import { AppTestingModule } from './app.testing.module';
import { SwUpdate } from '@angular/service-worker';
import { MaintenanceIconComponent } from './core/components/header/maintenance-icon/maintenance-icon.component';
import { InfoIconComponent } from './core/form-components/info-icon/info-icon.component';
import { authServiceSpy } from './app.testing.mocks';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, HeaderComponent,
        MaintenanceIconComponent,
        UserComponent,
        LanguageSwitcherComponent,
        SideNavComponent,
        LoadingSpinnerComponent,
        InfoIconComponent],
    declarations: [AppComponent],
    providers: [
        { provide: AuthService, useValue: authServiceSpy },
        {
            provide: SwUpdate,
            useValue: {},
        },
    ],
}).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should serialize date correctly', () => {
    const result = new Date('2029-06-01').toISOString();
    expect(result).toBe('2029-06-01');
  });
});
