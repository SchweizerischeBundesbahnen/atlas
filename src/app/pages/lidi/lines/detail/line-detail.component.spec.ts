import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EMPTY, of, throwError } from 'rxjs';
import { LinesService, LineVersion } from '../../../../api/lidi';
import { LineDetailComponent } from './line-detail.component';
import { CoreModule } from '../../../../core/module/core.module';

const lineVersion: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  shortName: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

let component: LineDetailComponent;
let fixture: ComponentFixture<LineDetailComponent>;
let router: Router;

describe('LineDetailComponent for existing lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', [
    'updateLineVersion',
    'deleteLineVersion',
  ]);
  const mockRoute = {
    snapshot: {
      data: {
        lineDetail: lineVersion,
      },
    },
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockRoute);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update LineVersion successfully', () => {
    mockLinesService.updateLineVersion.and.returnValue(of(lineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    const error = new Error('404');
    mockLinesService.updateLineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.EDIT_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should delete LineVersion successfully', () => {
    mockLinesService.deleteLineVersion.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not delete Version', () => {
    const error = new Error('404');
    mockLinesService.deleteLineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.DELETE_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });
});

describe('LineDetailComponent for new lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', ['createLineVersion']);
  const mockRoute = {
    snapshot: {
      data: {
        lineDetail: 'add',
      },
    },
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockRoute);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockLinesService.createLineVersion.and.returnValue(of(lineVersion));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      const err = new Error('404');
      mockLinesService.createLineVersion.and.returnValue(throwError(() => err));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.ADD_ERROR');
      expect(snackBarContainer.classList).toContain('error');
    });
  });
});

function setupTestBed(
  linesService: LinesService,
  activatedRoute: { snapshot: { data: { lineDetail: string | LineVersion } } }
) {
  TestBed.configureTestingModule({
    declarations: [LineDetailComponent],
    imports: [
      CoreModule,
      RouterModule.forRoot([]),
      HttpClientTestingModule,
      BrowserAnimationsModule,
      TranslateModule.forRoot({
        loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
      }),
    ],
    providers: [
      { provide: FormBuilder },
      { provide: LinesService, useValue: linesService },
      {
        provide: ActivatedRoute,
        useValue: activatedRoute,
      },
    ],
  })
    .compileComponents()
    .then();
}
