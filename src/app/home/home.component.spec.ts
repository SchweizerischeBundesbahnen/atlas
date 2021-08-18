import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SbbIconTestingModule } from '@sbb-esta/angular-core/icon/testing';

import { SbbAngularLibraryModule } from '../shared/sbb-angular-library.module';
import { HomeComponent } from './home.component';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService } from '../api';

const authServiceMock: Partial<AuthService> = {
  loggedIn: true,
};

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SbbAngularLibraryModule, SbbIconTestingModule],
      declarations: [HomeComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        {
          provide: TimetableFieldNumbersService,
          useValue: jasmine.createSpyObj('TimetableFieldNumbersService', ['getVersions']),
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
