import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SublineTableComponent } from './subline-table.component';
import { Line } from '../../../../../api';
import { of, Subject } from 'rxjs';
import { LineInternalService } from '../../../../../api/service/lidi/line-internal.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { FormatPipe } from '../../../../../core/components/table/pipe/format.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { mock } from 'vitest-mock-extended';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { inputBinding } from '@angular/core';

const subline: Line = {
  swissLineNumber: 'IC6',
  description: 'Subline 1',
  elementType: 'SUBLINE',
  status: 'VALIDATED',
  lidiElementType: 'CONCESSION',
  slnid: 'ch:1:slnid:8000:1',
  businessOrganisation: 'ch:1:sboid:123',
  validFrom: new Date('2021-12-31'),
  validTo: new Date('2099-12-31'),
};

describe('SublineTableComponent', () => {
  let component: SublineTableComponent;
  let fixture: ComponentFixture<SublineTableComponent>;
  let eventSubject: Subject<boolean>;
  let lineInternalService: Mocked<Pick<LineInternalService, 'getLines'>>;

  beforeEach(() => {
    lineInternalService = {
      getLines: vi.fn(),
    };
    lineInternalService.getLines.mockReturnValue(of({ objects: [subline] }));

    TestBed.configureTestingModule({
      providers: [
        { provide: LineInternalService, useValue: lineInternalService },
        {
          provide: TranslatePipe,
          useValue: mock<TranslatePipe>(),
        },
        translateServiceProvider,
        FormatPipe,
      ],
    });

    const mainLineSlnidInputName: keyof SublineTableComponent = 'mainLineSlnid';
    fixture = TestBed.createComponent(SublineTableComponent, {
      bindings: [inputBinding(mainLineSlnidInputName, () => 'ch:1:slnid:1')],
    });
    component = fixture.componentInstance;
    eventSubject = new Subject<boolean>();
    component.eventSubject = eventSubject;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sublines from backend', () => {
    component.getOverview();
    expect(lineInternalService.getLines).toHaveBeenCalled();
  });

  it('should navigate to subline in new tab', () => {
    const openSpy = vi.spyOn(window, 'open').mockImplementation(() => null);

    component.rowClicked(subline);

    expect(openSpy).toHaveBeenCalledExactlyOnceWith('/line-directory/sublines/ch:1:slnid:8000:1', '_blank');
  });
});
