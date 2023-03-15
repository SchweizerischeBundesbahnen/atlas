import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableSearchComponent } from './table-search.component';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import moment from 'moment';
import { Status } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { DateIconComponent } from '../../form-components/date-icon/date-icon.component';
import { MockBoSelectComponent } from '../../../app.testing.mocks';

describe('TableSearchComponent', () => {
  let component: TableSearchComponent;
  let fixture: ComponentFixture<TableSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TableSearchComponent, DateIconComponent, MockBoSelectComponent],
      imports: [AppTestingModule],
      providers: [TranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change activeStatuses on selection changes', () => {
    spyOn(component.searchEvent, 'emit');
    const statusSelectTrigger = fixture.debugElement.query(
      By.css('mat-select[data-cy="table-search-status-input"] div')
    );
    statusSelectTrigger.nativeElement.click();
    fixture.detectChanges();

    const statusOption = fixture.debugElement.query(By.css('mat-option'));
    statusOption.nativeElement.click();
    fixture.detectChanges();
    expect(component.activeStatuses).toEqual([Status.Draft]);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith({
      searchCriteria: [],
      validOn: undefined,
      statusChoices: [Status.Draft],
      boChoice: null,
    });

    statusOption.nativeElement.click();
    fixture.detectChanges();
    expect(component.activeStatuses).toEqual([]);
    expect(component.searchEvent.emit).toHaveBeenCalledTimes(2);
  });

  it('should emit event on valid Date', () => {
    spyOn(component.searchEvent, 'emit');
    const dateControl = fixture.componentInstance.dateControl;
    dateControl.setValue(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    const spy = jasmine.createSpyObj([], {
      value: moment('31.12.2021', 'DD.MM.yyyy'),
    });
    component.onDateChanged(spy);
    expect(component.searchDate).toEqual(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith({
      searchCriteria: [],
      validOn: moment('31.12.2021', 'DD.MM.yyyy').toDate(),
      statusChoices: [],
      boChoice: null,
    });
  });

  it('should not emit event on invalid Date', () => {
    spyOn(component.searchEvent, 'emit');
    const dateControl = fixture.componentInstance.dateControl;
    dateControl.setValue(moment('31.12.1600', 'DD.MM.yyyy').toDate());
    const spy = jasmine.createSpyObj([], {
      value: moment('31.12.1600', 'DD.MM.yyyy'),
    });
    component.onDateChanged(spy);
    expect(component.searchDate).not.toBeDefined();
    expect(component.searchEvent.emit).not.toHaveBeenCalled();
  });

  it('should add Search', () => {
    spyOn(component.searchEvent, 'emit');
    const spy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: () => undefined,
      },
    });
    component.addSearch(spy);
    expect(component.searchStrings).toEqual(['Test']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith({
      searchCriteria: ['Test'],
      validOn: undefined,
      statusChoices: [],
      boChoice: null,
    });
  });

  it("should not add search if it's already there", () => {
    spyOn(component.searchEvent, 'emit');
    const spy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: jasmine.createSpyObj(['clear']),
    });
    component.searchStrings.push('Test');
    component.addSearch(spy);
    expect(component.searchStrings).toEqual(['Test']);
    expect(component.searchEvent.emit).not.toHaveBeenCalled();
    expect(spy.chipInput.clear).toHaveBeenCalled();
  });

  it('should remove search', () => {
    spyOn(component.searchEvent, 'emit');
    component.searchStrings.push('Test', 'Test2');
    component.removeSearch('Test');
    expect(component.searchStrings).toEqual(['Test2']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith({
      searchCriteria: ['Test2'],
      validOn: undefined,
      statusChoices: [],
      boChoice: null,
    });
  });
});
