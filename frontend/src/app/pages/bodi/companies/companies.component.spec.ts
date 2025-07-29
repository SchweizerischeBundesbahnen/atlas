import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { CompaniesComponent } from './companies.component';
import { ContainerCompany } from '../../../api';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { CompanyInternalService } from '../../../api/service/bodi/company-internal.service';
import SpyObj = jasmine.SpyObj;

const company: ContainerCompany = {
  objects: [
    {
      uicCode: '1',
      name: 'Geilste Company',
    },
  ],
  totalCount: 1,
};

describe('CompaniesComponent', () => {
  let component: CompaniesComponent;
  let fixture: ComponentFixture<CompaniesComponent>;

  let companyInternalServiceSpy: SpyObj<CompanyInternalService>;

  beforeEach(() => {
    companyInternalServiceSpy = jasmine.createSpyObj<CompanyInternalService>({
      getCompanies: of(company),
    });

    TestBed.configureTestingModule({
      imports: [CompaniesComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        RouterOutlet,
        {
          provide: CompanyInternalService,
          useValue: companyInternalServiceSpy,
        },
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

    expect(companyInternalServiceSpy.getCompanies).toHaveBeenCalledOnceWith(
      [],
      0,
      10,
      ['uicCode,asc']
    );

    expect(component.companies.length).toEqual(1);
    expect(component.companies[0].uicCode).toEqual('1');
    expect(component.totalCount).toEqual(1);
  });
});
