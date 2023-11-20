import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { TransportCompaniesComponent } from './transport-companies.component';
import {
  ContainerTransportCompany,
  TransportCompaniesService,
  TransportCompanyStatus,
} from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

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

  let transportCompaniesServiceSpy: SpyObj<TransportCompaniesService>;

  beforeEach(() => {
    transportCompaniesServiceSpy = jasmine.createSpyObj<TransportCompaniesService>(
      'TransportCompaniesServiceSpy',
      ['getTransportCompanies'],
    );
    (
      transportCompaniesServiceSpy.getTransportCompanies as Spy<
        () => Observable<ContainerTransportCompany>
      >
    ).and.returnValue(of(transportCompany));

    TestBed.configureTestingModule({
      declarations: [TransportCompaniesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: TransportCompaniesService, useValue: transportCompaniesServiceSpy },
        TranslatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransportCompaniesComponent);
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

    expect(transportCompaniesServiceSpy.getTransportCompanies).toHaveBeenCalledOnceWith(
      [],
      [
        TransportCompanyStatus.Current,
        TransportCompanyStatus.OperatingPart,
        TransportCompanyStatus.Operator,
        TransportCompanyStatus.Supervision,
      ],
      0,
      10,
      ['number,asc'],
    );

    expect(component.transportCompanies.length).toEqual(1);
    expect(component.transportCompanies[0].id).toEqual(1);
    expect(component.totalCount).toEqual(1);
  });
});
