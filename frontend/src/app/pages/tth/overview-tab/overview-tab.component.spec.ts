import { OverviewTabComponent } from './overview-tab.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { OverviewToTabShareDataService } from './service/overview-to-tab-share-data.service';
import { HearingStatus } from '../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { Pages } from '../../pages';
import { PermissionService } from '../../../core/auth/permission/permission.service';

describe('OverviewTabComponent', () => {
  let component: OverviewTabComponent;
  let fixture: ComponentFixture<OverviewTabComponent>;
  let routerEventsSubject: Subject<Event>;
  let permissionServiceSpy: Mocked<Pick<PermissionService, 'getTthApplicationUserType'>>;

  beforeEach(async () => {
    routerEventsSubject = new Subject();

    permissionServiceSpy = {
      getTthApplicationUserType: vi.fn(),
    };

    const routerSpy: Mocked<Pick<Router, 'navigate'>> & {
      events: Subject<Event>;
    } = {
      navigate: vi.fn(),
      events: routerEventsSubject,
    };

    await TestBed.configureTestingModule({
      imports: [OverviewTabComponent],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: PermissionService, useValue: permissionServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: { canton: 'ch' },
            },
            firstChild: {
              snapshot: {
                data: { hearingStatus: HearingStatus.Active },
              },
            },
          },
        },
        OverviewToTabShareDataService,
      ],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(OverviewTabComponent);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should define two tabs with correct links and titles for Canton', () => {
    fixture = TestBed.createComponent(OverviewTabComponent);
    component = fixture.componentInstance;

    expect(component.TABS.length).toBe(2);
    expect(component.TABS[0]).toEqual({
      link: Pages.TTH_STATEMENTS.path,
      title: 'TTH.TAB.STATEMENTS',
    });
    expect(component.TABS[1]).toEqual({
      link: Pages.TTH_DOSSIERS.path,
      title: 'TTH.TAB.DOSSIERS',
    });
  });

  it('should define only one tab for BO_TTH', () => {
    permissionServiceSpy.getTthApplicationUserType.mockReturnValue('BO_TTH');

    fixture = TestBed.createComponent(OverviewTabComponent);
    component = fixture.componentInstance;

    expect(component.TABS.length).toBe(1);
    expect(component.TABS[0]).toEqual({
      link: Pages.TTH_DOSSIERS.path,
      title: 'TTH.TAB.DOSSIERS',
    });
  });
});
