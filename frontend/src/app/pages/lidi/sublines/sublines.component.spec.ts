import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { ContainerSubline, Status, SublinesService, SublineType } from '../../../api';
import { SublinesComponent } from './sublines.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

const versionContainer: ContainerSubline = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      status: Status.Validated,
      businessOrganisation: 'SBB',
      swissSublineNumber: 'L1:2',
      sublineType: SublineType.Technical,
    },
  ],
  totalCount: 1,
};

describe('SublinesComponent', () => {
  let component: SublinesComponent;
  let fixture: ComponentFixture<SublinesComponent>;

  let sublinesServiceSpy: SpyObj<SublinesService>;

  beforeEach(() => {
    sublinesServiceSpy = jasmine.createSpyObj<SublinesService>('SublinesServiceSpy', [
      'getSublines',
    ]);
    (sublinesServiceSpy.getSublines as Spy<() => Observable<ContainerSubline>>).and.returnValue(
      of(versionContainer)
    );

    TestBed.configureTestingModule({
      declarations: [SublinesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: SublinesService, useValue: sublinesServiceSpy }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(SublinesComponent);
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

    expect(sublinesServiceSpy.getSublines).toHaveBeenCalledOnceWith(
      [],
      [Status.Draft, Status.Validated, Status.InReview, Status.Withdrawn],
      [],
      undefined,
      undefined,
      0,
      10,
      ['slnid,asc']
    );

    expect(component.sublines.length).toEqual(1);
    expect(component.sublines[0].slnid).toEqual('slnid');
    expect(component.totalCount$).toEqual(1);
  });
});
