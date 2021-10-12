import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MaterialModule } from '../../../core/module/material.module';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../../../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { LinesComponent } from './lines.component';
import { LinesService, VersionsContainerLineVersion } from '../../../api/lidi';
import { LidiModule } from '../lidi.module';
import { CoreModule } from '../../../core/module/core.module';

const versionContainer: VersionsContainerLineVersion = {
  versions: [
    {
      id: 1,
      slnid: 'slnid',
      shortName: 'name',
      description: 'asdf',
      status: 'ACTIVE',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
    },
  ],
  totalCount: 1,
};

describe('LinesComponent', () => {
  let component: LinesComponent;
  let fixture: ComponentFixture<LinesComponent>;

  // With Spy
  const linesService = jasmine.createSpyObj('linesService', ['getLineVersions']);
  linesService.getLineVersions.and.returnValue(of(versionContainer));
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
    expect(linesService.getLineVersions).toHaveBeenCalled();

    expect(component.lineVersions.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
