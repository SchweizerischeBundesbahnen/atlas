import { TableContentPaginationAndSorting } from './table-content-pagination-and-sorting';

const objectsToSort = [
  {
    designation: 'A',
    number: 2,
    date: new Date('2022-06-03'),
  },
  {
    designation: 'B',
    number: 3,
    date: new Date('2022-06-01'),
  },
  {
    designation: 'C',
    number: 1,
    date: new Date('2022-06-02'),
  },
];

describe('TableContentPaginationAndSorting', () => {
  it('should sort by default text', () => {
    const sortedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      { page: 0, size: 5 },
      'designation',
    );
    expect(sortedObjects.map((i) => i.designation)).toEqual(['A', 'B', 'C']);
  });

  it('should sort by attribute text', () => {
    const sortedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 5,
        sort: 'designation,asc',
      },
      'number',
    );
    expect(sortedObjects.map((i) => i.designation)).toEqual(['A', 'B', 'C']);
  });

  it('should sort by attribute number asc', () => {
    const sortedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 5,
        sort: 'number,asc',
      },
      'designation',
    );
    expect(sortedObjects.map((i) => i.number)).toEqual([1, 2, 3]);
  });

  it('should sort by attribute number desc', () => {
    const sortedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 5,
        sort: 'number,desc',
      },
      'designation',
    );
    expect(sortedObjects.map((i) => i.number)).toEqual([3, 2, 1]);
  });

  it('should sort by attribute date asc', () => {
    const sortedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 5,
        sort: 'date,asc',
      },
      'designation',
    );
    expect(sortedObjects.map((i) => i.date.toISOString())).toEqual([
      '2022-06-01',
      '2022-06-02',
      '2022-06-03',
    ]);
  });

  it('should page through correctly', () => {
    let pagedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 1,
      },
      'designation',
    );
    expect(pagedObjects.length).toBe(1);
    expect(pagedObjects[0].designation).toBe('A');

    pagedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 1,
        size: 1,
      },
      'designation',
    );
    expect(pagedObjects.length).toBe(1);
    expect(pagedObjects[0].designation).toBe('B');

    pagedObjects = TableContentPaginationAndSorting.pageAndSort(
      objectsToSort,
      {
        page: 0,
        size: 2,
      },
      'designation',
    );
    expect(pagedObjects.length).toBe(2);
    expect(pagedObjects[0].designation).toBe('A');
    expect(pagedObjects[1].designation).toBe('B');
  });
});
