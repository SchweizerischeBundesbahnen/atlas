import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserComponent } from './user.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../auth.service';
import { By } from '@angular/platform-browser';
import { SbbUsermenuItem } from '@sbb-esta/angular-business/usermenu';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;

  const authServiceMock: Partial<AuthService> = {
    claims: { name: 'Test', email: 'test@test.ch', roles: [] },
    logout: () => Promise.resolve(true),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserComponent],
      imports: [
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render username', () => {
    expect(fixture.nativeElement.querySelector('button > span').textContent).toContain(
      authServiceMock.claims!.name
    );
  });

  it('should logout', () => {
    const logoutSpy = spyOn(authServiceMock as AuthService, 'logout');

    // Open user menu
    const usermenuOpenButton = fixture.debugElement.query(By.css('button'));
    usermenuOpenButton.nativeElement.click();
    fixture.detectChanges();

    // Logout
    const logoutButton = fixture.debugElement.query(By.css('#logout'));
    logoutButton.nativeElement.click();

    expect(logoutSpy).toHaveBeenCalled();
  });
});
