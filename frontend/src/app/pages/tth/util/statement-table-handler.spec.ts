import { describe, expect, it } from 'vitest';
import { StatementTableHandler } from './statement-table-handler';

describe('StatementTableHandler', () => {
  class ComponentWithStatementTable extends StatementTableHandler {}

  const statementTable = new ComponentWithStatementTable();

  it('should compute data protection icon', () => {
    //when
    let result = statementTable.computeDataProtectionIcon(true);
    //then
    expect(result).toEqual('bi bi-check color-success');

    result = statementTable.computeDataProtectionIcon(false);
    //then
    expect(result).toEqual('bi bi-x color-error');
  });

  it('should return true if documents are present', () => {
    //when
    let result = statementTable.isDocumentExisting([]);
    //then
    expect(result).toBe(false);

    result = statementTable.isDocumentExisting([{ fileName: 'file1', fileSize: 1 }]);
    //then
    expect(result).toBe(true);
  });

  it('should map last name', () => {
    //when
    const result = statementTable.mapToLastname({ lastName: 'Ueli' });
    //then
    expect(result).toEqual('Ueli');
  });
});
