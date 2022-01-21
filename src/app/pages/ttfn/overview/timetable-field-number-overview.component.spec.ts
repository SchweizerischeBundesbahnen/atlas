import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberOverviewComponent } from './timetable-field-number-overview.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TimetableFieldNumberContainer, TimetableFieldNumbersService } from '../../../api';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { CoreModule } from '../../../core/module/core.module';

const timetableFieldNumberContainer: TimetableFieldNumberContainer = {
  fieldNumbers: [
    {
      ttfnid: 'ttfnid',
      description: 'description',
      swissTimetableFieldNumber: 'asdf',
      status: 'ACTIVE',
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
        LoadingSpinnerComponent,
      ],
      imports: [
        CoreModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TimetableFieldNumbersService, useValue: timetableFieldNumberService }],
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
