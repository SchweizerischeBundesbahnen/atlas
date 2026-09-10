import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { MeanOfTransport } from '../../../../../api';
import { LineDetailFormComponent } from './line-detail-form.component';

describe('LineDetailFormComponent', () => {
  function createComponent(): LineDetailFormComponent {
    return TestBed.runInInjectionContext(() => new LineDetailFormComponent());
  }

  function trainCategories(): string[] {
    const trainGroup = createComponent().OFFER_CATEGORY_GROUP.find(
      (group) => group.name === MeanOfTransport.Train,
    );
    if (!trainGroup) {
      throw new Error('Train offer category group missing');
    }
    return trainGroup.category.map((category) => category.value);
  }

  it('should offer ES for mean of transport train', () => {
    expect(trainCategories()).toContain('ES');
  });

  it('should order ES directly after EN', () => {
    const categories = trainCategories();

    expect(categories.indexOf('ES')).toBe(categories.indexOf('EN') + 1);
  });
});
