import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../../../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { ContainerSubline, SublinesService, SublineVersion } from '../../../api';
import { SublinesComponent } from './sublines.component';
import { CoreModule } from '../../../core/module/core.module';
import StatusEnum = SublineVersion.StatusEnum;
import TypeEnum = SublineVersion.TypeEnum;

const versionContainer: ContainerSubline = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      status: StatusEnum.Active,
      businessOrganisation: 'SBB',
      swissSublineNumber: 'L1:2',
      type: TypeEnum.Technical,
    },
  ],
  totalCount: 1,
};

describe('SublinesComponent', () => {
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  // With Spy
  const sublinesService = jasmine.createSpyObj('linesService', ['getSublines']);
  sublinesService.getSublines.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SublinesComponent, TableComponent, LoadingSpinnerComponent],
      imports: [
        CoreModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: SublinesService, useValue: sublinesService }],
    }).compileComponents();

    fixture = TestBed.createComponent(SublinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(sublinesService.getSublines).toHaveBeenCalledOnceWith(
      undefined,
      undefined,
      [],
      undefined,
      0,
      10,
      ['swissSublineNumber,ASC']
    );
    expect(component.sublines.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
