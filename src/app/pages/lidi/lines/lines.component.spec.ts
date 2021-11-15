import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../../../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { LinesComponent } from './lines.component';
import { ContainerLine, LinesService, LineVersion } from '../../../api/lidi';
import { CoreModule } from '../../../core/module/core.module';
import TypeEnum = LineVersion.TypeEnum;

const versionContainer: ContainerLine = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      status: 'ACTIVE',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'SBB',
      swissLineNumber: 'L1',
      type: TypeEnum.Orderly,
    },
  ],
  totalCount: 1,
};

describe('LinesComponent', () => {
  let component: LinesComponent;
  let fixture: ComponentFixture<LinesComponent>;

  // With Spy
  const linesService = jasmine.createSpyObj('linesService', ['getLines']);
  linesService.getLines.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LinesComponent, TableComponent, LoadingSpinnerComponent],
      imports: [
        CoreModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: LinesService, useValue: linesService }],
    }).compileComponents();

    fixture = TestBed.createComponent(LinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(linesService.getLines).toHaveBeenCalled();

    expect(component.lineVersions.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
