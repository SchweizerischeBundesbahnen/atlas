import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, Subject } from 'rxjs';
import { CompaniesComponent } from './companies.component';
import { CompaniesService, ContainerCompany } from '../../../api';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
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
    companiesServiceSpy = jasmine.createSpyObj<CompaniesService>(
      'CompaniesServiceSpy',
      ['getCompanies']
    );
    (
      companiesServiceSpy.getCompanies as Spy<
        () => Observable<ContainerCompany>
      >
    ).and.returnValue(of(company));

    TestBed.configureTestingModule({
      imports: [CompaniesComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        RouterOutlet,
        { provide: CompaniesService, useValue: companiesServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { paramMap: new Subject() },
        },
      ],
    })
      .overrideComponent(CompaniesComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents();

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

    expect(companiesServiceSpy.getCompanies).toHaveBeenCalledOnceWith(
      [],
      0,
      10,
      ['uicCode,asc']
    );

    expect(component.companies.length).toEqual(1);
    expect(component.companies[0].uicCode).toEqual(1);
    expect(component.totalCount).toEqual(1);
  });
});
