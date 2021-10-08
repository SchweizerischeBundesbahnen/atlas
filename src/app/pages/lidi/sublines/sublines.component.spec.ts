import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthService } from '../../../core/auth/auth.service';
import { TimetableFieldNumbersService, VersionsContainer } from '../../../api/ttfn';
import { MaterialModule } from '../../../core/module/material.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../../../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { SublinesComponent } from './sublines.component';

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
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  // With Spy
  const timetableFieldNumberService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'getVersions',
  ]);
  timetableFieldNumberService.getVersions.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SublinesComponent, TableComponent, LoadingSpinnerComponent],
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

    fixture = TestBed.createComponent(SublinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    fixture.detectChanges();
    expect(timetableFieldNumberService.getVersions).toHaveBeenCalled();

    expect(component.sublineVersions.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
