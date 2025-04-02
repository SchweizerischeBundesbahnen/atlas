import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { StringListComponent } from './string-list.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';

describe('StringListComponent', () => {
  let component: StringListComponent;
  let fixture: ComponentFixture<StringListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StringListComponent, TranslateModule.forRoot()],
      providers: [TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(StringListComponent);
    component = fixture.componentInstance;
  });

  it('should throw when no initial value defined', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl(undefined),
    });
    component.controlName = 'emails';
    expect(fixture.detectChanges).toThrow();
  });

  it('should create', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl([]),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('get strListCtrl should throw when controlName not defined', () => {
    expect(fixture.detectChanges).toThrow();
  });

  it('get strListCtrl should throw when ctrl not defined', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl([]),
    });
    component.controlName = 'false';
    expect(fixture.detectChanges).toThrow();
  });

  it('should not add item when its already there', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl(['a@a.ch']),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: 'a@a.ch',
    });
    component.addItem();
    expect(component.formGroup.get(component.controlName)?.value).toEqual([
      'a@a.ch',
    ]);
    expect(component.formGroup.dirty).toBeFalse();
    expect(component.strListFormGroup.get('input')?.value).toEqual('');
  });

  it('should add item when its not already there', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl(['a@a.ch']),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: 'b@b.ch',
    });
    component.addItem();
    expect(component.formGroup.get(component.controlName)?.value).toEqual([
      'a@a.ch',
      'b@b.ch',
    ]);
    expect(component.formGroup.dirty).toBeTrue();
    expect(component.strListFormGroup.get('input')?.value).toEqual('');
  });

  it('should do nothing when input not valid', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl(['a@a.ch']),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: null,
    });
    component.addItem();
    expect(component.formGroup.get(component.controlName)?.value).toEqual([
      'a@a.ch',
    ]);
    expect(component.formGroup.dirty).toBeFalse();
    expect(component.strListFormGroup.get('input')?.value).toEqual(null);
  });

  it('should remove item', () => {
    component.formGroup = new FormGroup({
      emails: new FormControl(['a@a.ch']),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
    component.removeItem(0);
    expect(component.formGroup.get(component.controlName)?.value).toEqual([]);
    expect(component.formGroup.dirty).toBeTrue();
  });
});
