import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { LinesComponent } from './lines.component';
import { ContainerLine, LinesService, LineType, Status } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

const versionContainer: ContainerLine = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      status: 'VALIDATED',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'SBB',
      swissLineNumber: 'L1',
      lineType: LineType.Orderly,
    },
  ],
  totalCount: 1,
};

describe('LinesComponent', () => {
  let component: LinesComponent;
  let fixture: ComponentFixture<LinesComponent>;

  let linesServiceSpy: SpyObj<LinesService>;

  beforeEach(() => {
    linesServiceSpy = jasmine.createSpyObj<LinesService>('LinesServiceSpy', ['getLines']);
    (linesServiceSpy.getLines as Spy<() => Observable<ContainerLine>>).and.returnValue(
      of(versionContainer)
    );

    TestBed.configureTestingModule({
      declarations: [LinesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesServiceSpy }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(LinesComponent);
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

    expect(linesServiceSpy.getLines).toHaveBeenCalledOnceWith(
      undefined,
      [],
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn],
      [],
      undefined,
      undefined,
      0,
      10,
      ['slnid,asc']
    );

    expect(component.lineVersions.length).toEqual(1);
    expect(component.lineVersions[0].slnid).toEqual('slnid');
    expect(component.totalCount$).toEqual(1);
  });
});
