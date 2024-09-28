import { ParamsForTranslationPipe } from './params-for-translation.pipe';

describe('ParamsForTranslationPipe', () => {
  it('create an instance', () => {
    const pipe = new ParamsForTranslationPipe();
    expect(pipe).toBeTruthy();
  });

  it('should map Parameters to translation object', () => {
    const pipe = new ParamsForTranslationPipe();
    const translationObj = pipe.transform([
      {
        key: 'field',
        value: 'should be mapped',
      },
      {
        value: 'should be removed',
      },
    ]);
    expect(translationObj).toEqual({ field: 'should be mapped' });
  });
});
