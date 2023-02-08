import { TestBed } from '@angular/core/testing';

import { RouteToDialogComponent } from './route-to-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { of, Subject } from 'rxjs';
import { RouteToDialogService } from './route-to-dialog.service';

let matDialogOpenCount = 0;

class matDialogMock {
  open() {
    matDialogOpenCount++;
    return {
      afterClosed: () => of({}),
      close() {
        void 0;
      },
      componentInstance: {
        ngOnInit() {
          // Nothing to do here
        },
      },
    };
  }
}

describe('RouteToDialogComponent', () => {
  let component: RouteToDialogComponent;
  let routeToDialogService: RouteToDialogService;
  const dataSubject = new Subject<Data>();
  const route = { data: dataSubject };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RouteToDialogComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: Router,
          useValue: {
            navigate: () => {
              void 0;
            },
          },
        },
        { provide: ActivatedRoute, useValue: route },
        { provide: MatDialog, useValue: new matDialogMock() },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    routeToDialogService = TestBed.inject(RouteToDialogService);
    routeToDialogService.clearDialogRef();
    const fixture = TestBed.createComponent(RouteToDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    matDialogOpenCount = 0;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open dialog and set ref', () => {
    dataSubject.next({ component: {} });
    expect(routeToDialogService.getDialog()).toBeTruthy();
    expect(routeToDialogService.hasDialog()).toBeTruthy();
  });

  it('should re-open dialog using ref', () => {
    dataSubject.next({ component: {} });
    dataSubject.next({ component: {} });
    dataSubject.next({ component: {} });
    expect(matDialogOpenCount).toEqual(1);
  });

  it('should remove dialog on destroy', () => {
    dataSubject.next({ component: {} });
    expect(routeToDialogService.hasDialog()).toBeTruthy();
    component.ngOnDestroy();
    expect(routeToDialogService.hasDialog()).toBeFalsy();
  });

  it('should route unsubscribe on destroy', () => {
    dataSubject.next({ component: {} });
    expect(routeToDialogService.hasDialog()).toBeTruthy();
    component.ngOnDestroy();
    dataSubject.next({ component: {} });
    expect(routeToDialogService.hasDialog()).toBeFalsy();
  });
});
