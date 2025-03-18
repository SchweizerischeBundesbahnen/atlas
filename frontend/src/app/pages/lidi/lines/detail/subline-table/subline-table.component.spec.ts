import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SublineTableComponent } from './subline-table.component';
import { Line, LinesService } from '../../../../../api';
import { of, Subject } from 'rxjs';
import { MockTableComponent } from '../../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../../app.testing.module';

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

const linesService = jasmine.createSpyObj('LinesService', ['getLines']);
linesService.getLines.and.returnValue(of({ objects: subline }));

describe('SublineTableComponent', () => {
  let component: SublineTableComponent;
  let fixture: ComponentFixture<SublineTableComponent>;
  let eventSubject: Subject<boolean>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, SublineTableComponent, MockTableComponent],
    providers: [{ provide: LinesService, useValue: linesService }],
}).compileComponents();

    fixture = TestBed.createComponent(SublineTableComponent);
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
    expect(linesService.getLines).toHaveBeenCalled();
  });

  it('should navigate to subline in new tab', () => {
    spyOn(window, 'open');

    component.rowClicked(subline);
    expect(window.open).toHaveBeenCalledWith(
      '/line-directory/sublines/ch:1:slnid:8000:1',
      '_blank'
    );
  });
});
