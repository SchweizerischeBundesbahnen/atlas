import { describe, expect, it } from 'vitest';
import { FormControl, FormGroup } from '@angular/forms';
import { GlobalIdValidator } from './global-id-validator';
import { Country } from '../../../api';

function buildGroup(country: Country | null, globalId: string | null | undefined): FormGroup {
  const group = new FormGroup({
    country: new FormControl(country),
    globalId: new FormControl(globalId, GlobalIdValidator.countryPrefix),
  });
  group.controls.globalId.updateValueAndValidity();
  return group;
}

describe('GlobalIdValidator', () => {
  it('should return null when global id is empty', () => {
    const group = buildGroup('GERMANY', '');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should return null when country is not set', () => {
    const group = buildGroup(null, 'anything');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should accept a de: prefix for a German service point', () => {
    const group = buildGroup('GERMANY', 'de:05770:1282');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should accept a de: prefix for a German bus service point', () => {
    const group = buildGroup('GERMANY_BUS', 'de:05770:1282');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should reject a non de: prefix for a German service point', () => {
    const group = buildGroup('GERMANY', 'at:42:9379');
    expect(group.controls.globalId.errors).toEqual({ globalIdPrefixDe: true });
  });

  it('should accept an at: prefix for an Austrian service point', () => {
    const group = buildGroup('AUSTRIA', 'at:42:9379');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should accept an at: prefix for an Austrian bus service point', () => {
    const group = buildGroup('AUSTRIA_BUS', 'at:42:9379');
    expect(group.controls.globalId.errors).toBeNull();
  });

  it('should reject a non at: prefix for an Austrian service point', () => {
    const group = buildGroup('AUSTRIA', 'de:05770:1282');
    expect(group.controls.globalId.errors).toEqual({ globalIdPrefixAt: true });
  });
});
