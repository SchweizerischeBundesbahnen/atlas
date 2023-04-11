import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberOverviewComponent } from './timetable-field-number-overview.component';
import { Observable, of } from 'rxjs';
import { ContainerTimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth/auth.service';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MockTableComponent } from '../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';

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

  let timetableFieldNumberServiceSpy: SpyObj<TimetableFieldNumbersService>;

  beforeEach(() => {
    timetableFieldNumberServiceSpy = jasmine.createSpyObj<TimetableFieldNumbersService>(
      'TimetableFieldNumbersServiceSpy',
      ['getOverview']
    );
    (
      timetableFieldNumberServiceSpy.getOverview as Spy<
        () => Observable<ContainerTimetableFieldNumber>
      >
    ).and.returnValue(of(timetableFieldNumberContainer));

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
          useValue: timetableFieldNumberServiceSpy,
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

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(timetableFieldNumberServiceSpy.getOverview).toHaveBeenCalledOnceWith(
      [],
      undefined,
      undefined,
      undefined,
      DEFAULT_STATUS_SELECTION,
      0,
      10,
      ['ttfnid,asc']
    );

    expect(component.timetableFieldNumbers.length).toEqual(1);
    expect(component.timetableFieldNumbers[0].ttfnid).toEqual('ttfnid');
    expect(component.totalCount$).toEqual(1);
  });
});
