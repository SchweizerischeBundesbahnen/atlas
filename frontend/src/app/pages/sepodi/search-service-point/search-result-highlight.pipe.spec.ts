import { SearchResultHighlightPipe } from './search-result-highlight.pipe';

describe('SearchResultHighlightPipe', () => {
  it('create an instance', () => {
    const pipe = new SearchResultHighlightPipe();
    expect(pipe).toBeTruthy();
  });

  it('should highlight be', () => {
    const pipe = new SearchResultHighlightPipe();
    expect(pipe.transform('Bern', 'Be')).toBe('<b>Be</b>rn');
  });

  it('should not highlight when no match', () => {
    const pipe = new SearchResultHighlightPipe();
    expect(pipe.transform('Bern', 'as')).toBe('Bern');
  });

  it('should not highlight when no search input', () => {
    const pipe = new SearchResultHighlightPipe();
    expect(pipe.transform('Bern', '')).toBe('Bern');
  });

  it('should not highlight when no value input', () => {
    const pipe = new SearchResultHighlightPipe();
    expect(pipe.transform('', 'Bern')).toBe('Bern');
  });
});
