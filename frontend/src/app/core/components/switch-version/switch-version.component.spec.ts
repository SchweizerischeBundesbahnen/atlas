import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchVersionComponent } from './switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { Record } from '../base-detail/record';
import moment from 'moment';
import { AppTestingModule } from '../../../app.testing.module';

describe('SwitchVersionComponent', () => {
  let component: SwitchVersionComponent;
  let fixture: ComponentFixture<SwitchVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, SwitchVersionComponent],
      providers: [TranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SwitchVersionComponent);
    component = fixture.componentInstance;
    const firstRecord: Record = {
      id: 1,
      validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
      versionNumber: 1,
    };
    const secondRecord: Record = {
      id: 2,
      validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
      versionNumber: 2,
    };
    const thirdRecord: Record = {
      id: 3,
      validFrom: moment('1.6.2002', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
      versionNumber: 3,
    };
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    component.currentRecord = firstRecord;
    component.records = records;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('currentIndex should be 0 at start', () => {
    expect(component.currentIndex).toBe(0);
  });

  it('records should have versionName', () => {
    component.ngOnChanges();
    expect(component.records[0].versionNumber).toBe(1);
    expect(component.records[1].versionNumber).toEqual(2);
    expect(component.records[2].versionNumber).toEqual(3);
  });

  it('should switch to second version', () => {
    //given
    expect(component.isCurrentRecord(component.records[0])).toBeTrue();
    expect(component.getIndexOfRecord(component.records[0])).toBe(0);
    spyOn(component.switchVersion, 'emit');
    //when
    component.setCurrentRecord(component.records[1]);
    //then
    expect(component.currentIndex).toBe(1);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(1);
  });

  it('should switch to first version', () => {
    //given
    component.setCurrentRecord(component.records[1]);
    spyOn(component.switchVersion, 'emit');
    //when
    component.setCurrentRecord(component.records[0]);
    //then
    expect(component.currentIndex).toBe(0);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(0);
  });

  it('should evaluate gaps', () => {
    expect(component.hasGapToNextRecord(component.records[0])).toBeFalse();
    expect(component.hasGapToNextRecord(component.records[1])).toBeTrue();
    expect(component.hasGapToNextRecord(component.records[2])).toBeFalse();
  });
});
