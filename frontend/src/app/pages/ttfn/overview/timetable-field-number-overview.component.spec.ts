import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberOverviewComponent } from './timetable-field-number-overview.component';
import { of } from 'rxjs';
import { ContainerTimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth/auth.service';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MockTableComponent } from '../../../app.testing.mocks';

const timetableFieldNumberContainer: ContainerTimetableFieldNumber = {
  objects: [
    {
      ttfnid: 'ttfnid',
      description: 'description',
      swissTimetableFieldNumber: 'asdf',
      number: 'number',
      businessOrganisation: 'businessOrganisation',
      status: 'VALIDATED',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
    },
  ],
  totalCount: 1,
};

describe('TimetableFieldNumberOverviewComponent', () => {
  let component: TimetableFieldNumberOverviewComponent;
  let fixture: ComponentFixture<TimetableFieldNumberOverviewComponent>;

  // With Spy
  const timetableFieldNumberService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'getOverview',
  ]);
  timetableFieldNumberService.getOverview.and.returnValue(of(timetableFieldNumberContainer));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        TimetableFieldNumberOverviewComponent,
        MockTableComponent,
        AtlasButtonComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        {
          provide: TimetableFieldNumbersService,
          useValue: timetableFieldNumberService,
        },
        TranslatePipe,
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>('AuthService', ['hasPermissionsToCreate']),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableFieldNumberOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
