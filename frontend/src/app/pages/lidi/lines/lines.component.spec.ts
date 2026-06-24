import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { LinesComponent } from './lines.component';
import { ContainerLine, ElementType, LidiElementType, Line, Status } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';
import { TableComponent } from '../../../core/components/table/table.component';
import { LineInternalService } from '../../../api/service/lidi/line-internal.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

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
  let lineInternalService: Mocked<Pick<LineInternalService, 'getLines'>>;

  beforeEach(() => {
    lineInternalService = {
      getLines: vi.fn(),
    };
    lineInternalService.getLines.mockReturnValue(of(versionContainer));

    TestBed.configureTestingModule({
      imports: [LinesComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        { provide: LineInternalService, useValue: lineInternalService },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
      ],
    }).overrideComponent(LinesComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    });

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
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    //when
    component.editVersion(line);
    //then
    expect(navigateSpy).toHaveBeenCalledWith([Pages.LIDI.path, Pages.LINES.path, line.slnid]);
  });

  it('should edit subline', () => {
    //given
    line.elementType = 'SUBLINE';
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    //when
    component.editVersion(line);
    //then
    expect(navigateSpy).toHaveBeenCalledWith([Pages.LIDI.path, Pages.SUBLINES.path, line.slnid]);
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(lineInternalService.getLines).toHaveBeenCalledExactlyOnceWith(
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
      0,
      10,
      ['slnid,asc']
    );

    expect(component.lineVersions().length).toEqual(1);
    expect(component.lineVersions()[0].slnid).toEqual('slnid');
    expect(component.totalCount()).toEqual(1);
  });
});
