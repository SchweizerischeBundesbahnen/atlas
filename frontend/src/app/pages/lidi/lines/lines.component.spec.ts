import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { LinesComponent } from './lines.component';
import {
  ContainerLine,
  ElementType,
  LidiElementType,
  Line,
  Status,
} from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import { Router } from '@angular/router';
import { Pages } from '../../pages';
import { LineService } from '../../../api/service/lidi/line.service';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

const line: Line = {
  swissLineNumber: 'IC6',
  elementType: 'SUBLINE',
  status: 'VALIDATED',
  lidiElementType: 'CONCESSION',
  slnid: 'ch:1:slnid:8000',
  businessOrganisation: 'ch:1:sboid:123',
  validFrom: new Date('2021-12-31'),
  validTo: new Date('2099-12-31'),
};

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
      lidiElementType: LidiElementType.Orderly,
      elementType: ElementType.Line,
    },
  ],
  totalCount: 1,
};

describe('LinesComponent', () => {
  let component: LinesComponent;
  let fixture: ComponentFixture<LinesComponent>;
  let router: Router;

  let lineServiceSpy: SpyObj<LineService>;

  beforeEach(() => {
    lineServiceSpy = jasmine.createSpyObj<LinesService>('LinesServiceSpy', [
      'getLines',
    ]);
    (
      lineServiceSpy.getLines as Spy<() => Observable<ContainerLine>>
    ).and.returnValue(of(versionContainer));

    TestBed.configureTestingModule({
      declarations: [LinesComponent],
      imports: [LinesComponent, TranslateModule.forRoot()],
      providers: [
        TranslatePipe,
        { provide: LineService, useValue: lineServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: { paramMap: new Subject() },
        },
      ],
    })
      .overrideComponent(LinesComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(LinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should edit line', () => {
    //given
    line.elementType = 'LINE';
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    //when
    component.editVersion(line);
    //then
    expect(router.navigate).toHaveBeenCalledWith([
      Pages.LIDI.path,
      Pages.LINES.path,
      line.slnid,
    ]);
  });

  it('should edit subline', () => {
    //given
    line.elementType = 'SUBLINE';
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    //when
    component.editVersion(line);
    //then
    expect(router.navigate).toHaveBeenCalledWith([
      Pages.LIDI.path,
      Pages.SUBLINES.path,
      line.slnid,
    ]);
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(lineServiceSpy.getLines).toHaveBeenCalledOnceWith(
      undefined,
      [],
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn],
      [],
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
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
