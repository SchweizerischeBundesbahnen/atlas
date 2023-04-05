import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TransportCompaniesComponent } from './transport-companies.component';
import { ContainerTransportCompany, TransportCompaniesService } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';

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

  // With Spy
  const transportCompaniesService = jasmine.createSpyObj('transportCompaniesService', [
    'getTransportCompanies',
  ]);
  transportCompaniesService.getTransportCompanies.and.returnValue(of(transportCompany));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransportCompaniesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: TransportCompaniesService, useValue: transportCompaniesService },
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
});
