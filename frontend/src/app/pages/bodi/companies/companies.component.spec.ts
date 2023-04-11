import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { CompaniesComponent } from './companies.component';
import { CompaniesService, ContainerCompany } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

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

  let companiesServiceSpy: SpyObj<CompaniesService>;

  beforeEach(() => {
    companiesServiceSpy = jasmine.createSpyObj<CompaniesService>('CompaniesServiceSpy', [
      'getCompanies',
    ]);
    (companiesServiceSpy.getCompanies as Spy<() => Observable<ContainerCompany>>).and.returnValue(
      of(company)
    );

    TestBed.configureTestingModule({
      declarations: [CompaniesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: CompaniesService, useValue: companiesServiceSpy }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(CompaniesComponent);
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

    expect(companiesServiceSpy.getCompanies).toHaveBeenCalledOnceWith([], 0, 10, ['uicCode,asc']);

    expect(component.companies.length).toEqual(1);
    expect(component.companies[0].uicCode).toEqual(1);
    expect(component.totalCount).toEqual(1);
  });
});
