import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RelationComponent } from './relation.component';
import { By } from '@angular/platform-browser';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';
import { TableColumn } from '../table/table-column';

const tableColumns: TableColumn<{ [k: string]: unknown }>[] = [
  {
    headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
    valuePath: 'said',
    columnDef: 'said',
  },
  {
    headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
    valuePath: 'organisationNumber',
    columnDef: 'organisationNumber',
  },
  {
    headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
    valuePath: `abbreviationDe`,
    columnDef: 'abbreviation',
  },
  {
    headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
    valuePath: `descriptionDe`,
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
] as const;

describe('TransportCompanyRelationComponent', () => {
  let component: RelationComponent<unknown>;
  let fixture: ComponentFixture<RelationComponent<unknown>>;

  let tableColumnsInput: ReturnType<typeof signal<TableColumn<{ [k: string]: unknown }>[]>>;
  let editableInput: ReturnType<typeof signal<boolean>>;
  let selectedIndexInput: ReturnType<typeof signal<number>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    tableColumnsInput = signal(tableColumns);
    editableInput = signal(false);
    selectedIndexInput = signal(-1);

    const tableColumnsInputName: keyof RelationComponent<unknown> = 'tableColumns';
    const editableInputName: keyof RelationComponent<unknown> = 'editable';
    const selectedIndexInputName: keyof RelationComponent<unknown> = 'selectedIndex';
    fixture = TestBed.createComponent(RelationComponent, {
      bindings: [
        inputBinding(tableColumnsInputName, tableColumnsInput),
        inputBinding(editableInputName, editableInput),
        inputBinding(selectedIndexInputName, selectedIndexInput),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
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
    selectedIndexInput.set(1);
    fixture.detectChanges();
    expect(component.isRowSelected(component._records[1])).toBe(true);
    expect(component.isRowSelected(component._records[0])).toBe(false);
  });

  it('edit mode changed should emit event', () => {
    editableInput.set(true);
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
    editableInput.set(true);
    const indexPromise = new Promise((resolve) => {
      component.selectedIndexChanged.subscribe((selectedIndex) => {
        resolve(selectedIndex);
      });
    });
    fixture.detectChanges();
    component.selectRecord(component._records[1]);
    const index = await indexPromise;
    expect(index).toBe(1);

    editableInput.set(false);
    fixture.detectChanges();
    vi.spyOn(component.selectedIndexChanged, 'emit');
    component.selectRecord(component._records[0]);
    expect(component.selectedIndexChanged.emit).not.toHaveBeenCalled();
  });

  it('test delete', () => {
    editableInput.set(true);
    selectedIndexInput.set(0);
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[2];
    vi.spyOn(component.deleteRelation, 'emit').mockImplementation(() => {});
    deleteBtn.nativeElement.click();
    expect(component.deleteRelation.emit).toHaveBeenCalledExactlyOnceWith();
  });

  it('test update', () => {
    editableInput.set(true);
    selectedIndexInput.set(0);
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button'))[1];
    vi.spyOn(component.updateRelation, 'emit').mockImplementation(() => {});
    deleteBtn.nativeElement.click();
    expect(component.updateRelation.emit).toHaveBeenCalledExactlyOnceWith();
  });
});
