import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home.component';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService, VersionsContainer } from '../api';
import { MaterialModule } from '../core/module/material.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LanguageSwitcherComponent } from '../core/components/language-switcher/language-switcher.component';
import { RouterModule } from '@angular/router';

const authServiceMock: Partial<AuthService> = {
  loggedIn: true,
};

const versionContainer: VersionsContainer = {
  versions: [
    {
      id: 1,
      ttfnid: 'ttfnid',
      name: 'name',
      swissTimetableFieldNumber: 'asdf',
      status: 'ACTIVE',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
    },
  ],
  totalCount: 1,
};

// With Mock
// const timetableFieldNumberMockService : Partial<TimetableFieldNumbersService> = {
//   getVersions(): Observable<any> {
//     return of([versionContainer]);
//   }
// }

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  // With Spy
  const timetableFieldNumberService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'getVersions',
  ]);
  timetableFieldNumberService.getVersions.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HomeComponent, TableComponent, LanguageSwitcherComponent],
      imports: [
        MaterialModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: TimetableFieldNumbersService, useValue: timetableFieldNumberService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    fixture.detectChanges();
    expect(timetableFieldNumberService.getVersions).toHaveBeenCalled();

    expect(component.versions$.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
