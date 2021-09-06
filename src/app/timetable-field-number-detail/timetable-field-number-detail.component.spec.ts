import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TimetableFieldNumbersService, Version } from '../api';

const version: Version = {
  id: 1,
  ttfnid: 'ttfnid',
  name: 'name',
  swissTimetableFieldNumber: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

const routeSnapshotParams = {
  get: () => {},
};

const routeSnapshotMock = {
  snapshot: {
    paramMap: routeSnapshotParams,
    data: {
      timetableFieldNumberDetail: version,
    },
  },
};

describe('TimetableFieldNumberDetailComponent', () => {
  let component: TimetableFieldNumberDetailComponent;
  let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableFieldNumberDetailComponent],
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: FormBuilder },
        { provide: TimetableFieldNumbersService },
        {
          provide: ActivatedRoute,
          useValue: routeSnapshotMock,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
