import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TableComponent } from '../../../core/components/table/table.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { SublinesService, VersionsContainerSublineVersion } from '../../../api/lidi';
import { SublinesComponent } from './sublines.component';
import { CoreModule } from '../../../core/module/core.module';

const versionContainer: VersionsContainerSublineVersion = {
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

describe('SublinesComponent', () => {
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  // With Spy
  const sublinesService = jasmine.createSpyObj('linesService', ['getSublineVersions']);
  sublinesService.getSublineVersions.and.returnValue(of(versionContainer));
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
    expect(sublinesService.getSublineVersions).toHaveBeenCalled();

    expect(component.sublineVersions.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
