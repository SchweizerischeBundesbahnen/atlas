import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { TransportCompaniesComponent } from './transport-companies.component';
import { ContainerTransportCompany, TransportCompanyStatus } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { TransportCompanyService } from '../../../api/service/bodi/transport-company.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const transportCompany: ContainerTransportCompany = {
  objects: [
    {
      id: 1,
      number: '#0001',
    },
  ],
  totalCount: 1,
};

describe('TransportCompaniesComponent', () => {
  let component: TransportCompaniesComponent;
  let fixture: ComponentFixture<TransportCompaniesComponent>;
  let transportCompanyService: Mocked<Pick<TransportCompanyService, 'getTransportCompanies'>>;

  beforeEach(() => {
    transportCompanyService = {
      getTransportCompanies: vi.fn(),
    };
    transportCompanyService.getTransportCompanies.mockReturnValue(of(transportCompany));

    TestBed.configureTestingModule({
      imports: [TransportCompaniesComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        RouterOutlet,
        { provide: TransportCompanyService, useValue: transportCompanyService },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
      ],
    }).overrideComponent(TransportCompaniesComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    });

    fixture = TestBed.createComponent(TransportCompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(transportCompanyService.getTransportCompanies).toHaveBeenCalledExactlyOnceWith(
      [],
      [
        TransportCompanyStatus.Current,
        TransportCompanyStatus.OperatingPart,
        TransportCompanyStatus.Operator,
        TransportCompanyStatus.Supervision,
      ],
      0,
      10,
      ['number,asc']
    );

    expect(component.transportCompanies.length).toEqual(1);
    expect(component.transportCompanies[0].id).toEqual(1);
    expect(component.totalCount).toEqual(1);
  });
});
