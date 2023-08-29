import { FormControl } from '@angular/forms';
import { AtLeastOneValidator } from './at-least-one-validator';

describe('AtLeastOneValidator', () => {
  it('should return validation error when no checkbox is checked', () => {
    //given
    const stopPoint = new FormControl(false);
    const freightServicePoint = new FormControl(false);

    //when
    AtLeastOneValidator.validate(stopPoint, freightServicePoint);

    //then
    expect(stopPoint.errors).toBeDefined();
    const firstControlErrors = stopPoint.errors?.['at_least_one'];
    expect(firstControlErrors).toBeDefined();

    expect(freightServicePoint.errors).toBeDefined();
    const secondControlErrors = freightServicePoint.errors?.['at_least_one'];
    expect(secondControlErrors).toBeDefined();
  });

  it('should remove validation error when one box gets checked', () => {
    //given
    const stopPoint = new FormControl(false);
    const freightServicePoint = new FormControl(false);
    AtLeastOneValidator.validate(stopPoint, freightServicePoint);

    //when
    stopPoint.setValue(true);
    AtLeastOneValidator.validate(stopPoint, freightServicePoint);
    //then
    expect(stopPoint.errors).toBeNull();
    expect(freightServicePoint.errors).toBeNull();
  });
});
