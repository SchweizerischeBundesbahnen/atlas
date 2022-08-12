import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { CompaniesComponent } from './companies.component';
import { CompaniesService, ContainerCompany } from '../../../api';
import { AppTestingModule, MockAppTableSearchComponent } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';

const company: ContainerCompany = {
  objects: [
    {
      uicCode: 1,
      name: 'Geilste Company',
    },
  ],
  totalCount: 1,
};

describe('CompaniesComponent', () => {
  let component: CompaniesComponent;
  let fixture: ComponentFixture<CompaniesComponent>;

  // With Spy
  const companiesService = jasmine.createSpyObj('companiesService', ['getCompanies']);
  companiesService.getCompanies.and.returnValue(of(company));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        CompaniesComponent,
        TableComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: CompaniesService, useValue: companiesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(CompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(companiesService.getCompanies).toHaveBeenCalled();

    expect(component.companies.length).toBe(1);
    expect(component.totalCount).toBe(1);
  });
});
