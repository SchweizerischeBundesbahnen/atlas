import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberOverviewComponent } from './timetable-field-number-overview.component';
import { of } from 'rxjs';
import { ContainerTimetableFieldNumber, TimetableFieldNumbersService } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../../../core/auth/auth.service';
import { MockAppTableSearchComponent } from '../../../app.testing.mocks';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';

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
        TableComponent,
        AtlasButtonComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
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
    fixture.detectChanges();
    expect(timetableFieldNumberService.getOverview).toHaveBeenCalled();

    expect(component.timetableFieldNumbers.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
