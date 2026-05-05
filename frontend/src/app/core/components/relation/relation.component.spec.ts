import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RelationComponent } from './relation.component';
import { By } from '@angular/platform-browser';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('TransportCompanyRelationComponent', () => {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let component: RelationComponent<any>;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let fixture: ComponentFixture<RelationComponent<any>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    fixture = TestBed.createComponent(RelationComponent);
    component = fixture.componentInstance;
    component.tableColumns = [
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
        valuePath: 'businessOrganisation.said',
        columnDef: 'said',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
        valuePath: 'businessOrganisation.organisationNumber',
        columnDef: 'organisationNumber',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        valuePath: `businessOrganisation.abbreviationDe`,
        columnDef: 'abbreviation',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
        valuePath: `businessOrganisation.descriptionDe`,
        columnDef: 'description',
      },
      {
        headerTitle: 'COMMON.VALID_FROM',
        value: 'validFrom',
        columnDef: 'validFrom',
        formatAsDate: true,
      },
      {
        headerTitle: 'COMMON.VALID_TO',
        value: 'validTo',
        columnDef: 'validTo',
        formatAsDate: true,
      },
    ];
  });

  it('test columnValues function', () => {
    expect(component.columnValues()).toEqual([
      'said',
      'organisationNumber',
      'abbreviation',
      'description',
      'validFrom',
      'validTo',
    ]);
  });

  it('test isRowSelected function', () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.selectedIndex = 1;
    expect(component.isRowSelected(component._records[1])).toBe(true);
    expect(component.isRowSelected(component._records[0])).toBe(false);
  });

  it('edit mode changed should emit event', () => {
    component.editable = true;
    fixture.detectChanges();
    const editBtn = fixture.debugElement.query(By.css('button'));
    vi.spyOn(component.editModeChanged, 'emit').mockImplementation(() => {});
    editBtn.nativeElement.click();
    expect(component.editModeChanged.emit).toHaveBeenCalledExactlyOnceWith();
  });

  it('test select record', async () => {
    component.records = [
      { id: 1, value: 'test1' },
      { id: 2, value: 'test2' },
    ];
    component.editable = true;
    const indexPromise = new Promise((resolve) => {
      component.selectedIndexChanged.subscribe((selectedIndex) => {
        resolve(selectedIndex);
      });
    });
    component.selectRecord(component._records[1]);
    const index = await indexPromise;
    expect(index).toBe(1);

    component.editable = false;
    vi.spyOn(component.selectedIndexChanged, 'emit');
    component.selectRecord(component._records[0]);
    expect(component.selectedIndexChanged.emit).not.toHaveBeenCalled();
  });

  it('test delete', () => {
    component.editable = true;
    component.selectedIndex = 0;
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[2];
    vi.spyOn(component.deleteRelation, 'emit').mockImplementation(() => {});
    deleteBtn.nativeElement.click();
    expect(component.deleteRelation.emit).toHaveBeenCalledExactlyOnceWith();
  });

  it('test update', () => {
    component.editable = true;
    component.selectedIndex = 0;
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[1];
    vi.spyOn(component.updateRelation, 'emit').mockImplementation(() => {});
    deleteBtn.nativeElement.click();
    expect(component.updateRelation.emit).toHaveBeenCalledExactlyOnceWith();
  });
});
