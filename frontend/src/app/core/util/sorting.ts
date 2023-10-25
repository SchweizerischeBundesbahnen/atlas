import moment from 'moment';

export function naturalCompare(a: any, b: any, direction?: 'asc' | 'desc'): number {
  if (a == null) {
    return -1;
  }
  if (b == null) {
    return 1;
  }

  let result = valueCompare(a, b);

  if (direction === 'desc') {
    result = result * -1;
  }
  return result;
}

function valueCompare(a: any, b: any): number {
  let result;
  if (moment(a, 'yyyy-MM-DD', true).isValid() && moment(b, 'yyyy-MM-DD', true).isValid()) {
    result = moment(a).isAfter(moment(b)) ? 1 : -1;
  } else if (typeof a === 'string' && typeof b === 'string') {
    result = a.localeCompare(b);
  } else {
    result = a > b ? 1 : -1;
  }
  return result;
}
