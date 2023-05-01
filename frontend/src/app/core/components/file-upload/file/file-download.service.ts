export class FileDownloadService {
  static downloadFile(fileName: string, fileContent: Blob) {
    const a = document.createElement('a');
    a.download = fileName;
    a.href = URL.createObjectURL(fileContent);
    a.click();
  }
}
