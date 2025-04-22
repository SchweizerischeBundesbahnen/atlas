import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TimetableFieldNumberOverviewComponent } from './timetable-field-number-overview.component';
import { Observable, of, Subject } from 'rxjs';
import { ContainerTimetableFieldNumber } from '../../../api';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockTableComponent,
} from '../../../app.testing.mocks';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/timetable-field-number-internal.service';
import { ActivatedRoute } from '@angular/router';
import { TableComponent } from '../../../core/components/table/table.component';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

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

  let timetableFieldNumberServiceSpy: SpyObj<TimetableFieldNumberInternalService>;

  beforeEach(async () => {
    timetableFieldNumberServiceSpy =
      jasmine.createSpyObj<TimetableFieldNumberInternalService>(
        'TimetableFieldNumberInternalService',
        ['getOverview']
      );
    (
      timetableFieldNumberServiceSpy.getOverview as Spy<
        () => Observable<ContainerTimetableFieldNumber>
      >
    ).and.returnValue(of(timetableFieldNumberContainer));

    await TestBed.configureTestingModule({
      imports: [
        TimetableFieldNumberOverviewComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        {
          provide: TimetableFieldNumberInternalService,
          useValue: timetableFieldNumberServiceSpy,
        },
        TranslatePipe,
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
      ],
    })
      .overrideComponent(TimetableFieldNumberOverviewComponent, {
        remove: { imports: [AtlasButtonComponent, TableComponent] },
        add: { imports: [MockAtlasButtonComponent, MockTableComponent] },
      })
      .compileComponents();

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
