import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { TransportCompaniesComponent } from './transport-companies.component';
import { ContainerTransportCompany, TransportCompaniesService } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { Component, Input, TemplateRef } from '@angular/core';

const transportCompany: ContainerTransportCompany = {
  objects: [
    {
      id: 1,
      number: '#0001',
    },
  ],
  totalCount: 1,
};

@Component({
  selector: 'app-table-search',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppTableSearchComponent {
  @Input() additionalFieldTemplate!: TemplateRef<any>;
  @Input() displayStatus = true;
  @Input() displayValidOn = true;
  @Input() searchTextColumnStyle = 'col-4';
}

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
      declarations: [
        TransportCompaniesComponent,
        TableComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
      ],
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
    expect(transportCompaniesService.getTransportCompanies).toHaveBeenCalled();

    expect(component.transportCompanies.length).toBe(1);
    expect(component.totalCount).toBe(1);
  });
});
