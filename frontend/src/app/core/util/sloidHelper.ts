export function servicePointSloidToNumber(sloid: string): number {
  const lastPart = sloid.substring(sloid.lastIndexOf(':') + 1);
  if (lastPart.length != 7) {
    return 8500000 + Number(lastPart);
  }
  return Number(lastPart);
}
