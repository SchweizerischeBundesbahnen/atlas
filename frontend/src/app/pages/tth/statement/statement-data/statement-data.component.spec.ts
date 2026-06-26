import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StatementDataComponent } from './statement-data.component';
import {
  MockTimetableFieldNumberSelectComponent,
  MockTuSelectComponent,
  translateServiceProvider,
} from '../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { statement, statementFormGroup } from '../statement-test-util';
import { TransportCompanySelectComponent } from '../../../../core/form-components/tu-select/transport-company-select.component';
import { TimetableFieldNumberSelectComponent } from '../../../../core/form-components/ttfn-select/timetable-field-number-select.component';
import {
  TimetableFieldNumber,
  TimetableHearingStatementResponsibleTransportCompany,
  TransportCompany,
} from '../../../../api';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
};

const mockTimetableHearingStatementsService: Mocked<
  Pick<TimetableHearingStatementInternalService, 'getResponsibleTransportCompanies'>
> = {
  getResponsibleTransportCompanies: vi.fn().mockReturnValue(of([transportCompany])),
};
const router: Mocked<Pick<Router, 'navigate'>> = {
  navigate: vi.fn().mockResolvedValue(true),
};

describe('StatementData', () => {
  let component: StatementDataComponent;
  let fixture: ComponentFixture<StatementDataComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StatementDataComponent, MockTuSelectComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: TimetableHearingStatementInternalService,
          useValue: mockTimetableHearingStatementsService,
        },
        {
          provide: Router,
          useValue: router,
        },
        {
          provide: ActivatedRoute,
          useValue: {},
        },
      ],
    }).overrideComponent(StatementDataComponent, {
      remove: {
        imports: [TransportCompanySelectComponent, TimetableFieldNumberSelectComponent],
      },
      add: {
        imports: [MockTuSelectComponent, MockTimetableFieldNumberSelectComponent],
      },
    });

    fixture = TestBed.createComponent(StatementDataComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('form', statementFormGroup);
    fixture.componentRef.setInput('statement', statement);
    fixture.componentRef.setInput('ttfnValidOn', new Date());
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should ttfnSelectionChanged', () => {
    //given
    const ttfn: TimetableFieldNumber = {
      number: '30.300',
      status: 'DRAFT',
      businessOrganisation: 'ch:1:sboid:123',
      validFrom: new Date(),
      validTo: new Date(),
    };
    //when
    component.ttfnSelectionChanged(ttfn);
    //then
    expect(component.form().controls.responsibleTransportCompanies.value?.length).toBe(1);
    const transportCompanies: TimetableHearingStatementResponsibleTransportCompany[] | null | undefined = component
      .form()
      .controls.responsibleTransportCompanies.getRawValue();
    if (transportCompanies) {
      expect(transportCompanies.length).toBe(1);
      expect(transportCompanies[0]).toBe(transportCompany);
    }
  });
});
