import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { TimetableFieldNumberSelectComponent } from './timetable-field-number-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { of } from 'rxjs';
import { ContainerTimetableFieldNumber, Status, TimetableFieldNumber } from '../../../api';

const TTFNID = 'ch:1:ttfnid:100001';

const REFERENCED_TTFN: TimetableFieldNumber = {
  ttfnid: TTFNID,
  number: '1.1',
  descriptionOutwardLine1: 'Bern - Zuerich',
  status: Status.Validated,
  businessOrganisation: 'ch:1:sboid:100001',
  validFrom: new Date('2000-01-01'),
  validTo: new Date('2099-12-31'),
};

describe('TimetableFieldNumberSelectComponent', () => {
  let component: TimetableFieldNumberSelectComponent;
  let fixture: ComponentFixture<TimetableFieldNumberSelectComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;
  let timetableFieldNumberServiceMock: Mocked<Pick<TimetableFieldNumberInternalService, 'getOverview'>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        TimetableFieldNumberSelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [
        TranslatePipe,
        translateServiceProvider,
        provideHttpClientTesting(),
        {
          provide: TimetableFieldNumberInternalService,
          useValue: (timetableFieldNumberServiceMock = {
            getOverview: vi.fn(() =>
              of({ objects: [REFERENCED_TTFN], totalCount: 1 } as ContainerTimetableFieldNumber)
            ),
          }),
        },
      ],
    });

    const formGroupInputName: keyof TimetableFieldNumberSelectComponent = 'formGroup';
    const controlNameInputName: keyof TimetableFieldNumberSelectComponent = 'controlName';
    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    fixture = TestBed.createComponent(TimetableFieldNumberSelectComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not resolve anything when no ttfnid is preselected', () => {
    expect(timetableFieldNumberServiceMock.getOverview).not.toHaveBeenCalled();
  });

  it('should resolve preselected ttfnid via ttfnIds without validity restriction', () => {
    formGroupInput.set(
      new FormGroup({
        testControl: new FormControl(TTFNID),
      })
    );
    fixture.detectChanges();

    expect(timetableFieldNumberServiceMock.getOverview).toHaveBeenCalledWith(
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      ['ttfnid,ASC'],
      [TTFNID]
    );
  });

  it('should expose the resolved timetable field number', async () => {
    formGroupInput.set(
      new FormGroup({
        testControl: new FormControl(TTFNID),
      })
    );
    fixture.detectChanges();

    const timetableFieldNumbers = await new Promise<TimetableFieldNumber[]>((resolve) =>
      component.timetableFieldNumbers.subscribe(resolve)
    );
    expect(timetableFieldNumbers).toEqual([REFERENCED_TTFN]);
  });

  it('should search by free text without validity restriction', () => {
    component.searchTimetableFieldNumber('1.1');

    expect(timetableFieldNumberServiceMock.getOverview).toHaveBeenCalledWith(
      ['1.1'],
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      ['ttfnid,ASC']
    );
  });

  it('should not search when free text is empty', () => {
    component.searchTimetableFieldNumber('');

    expect(timetableFieldNumberServiceMock.getOverview).not.toHaveBeenCalled();
  });
});
