package ch.sbb.atlas.pdf.sanitize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;

@Slf4j
class PdfCdrEmbeddedFileSanitization {

  private final PDDocument doc;

  PdfCdrEmbeddedFileSanitization(PDDocument doc) {
    this.doc = doc;
  }

  void sanitize(PDEmbeddedFilesNameTreeNode embeddedFiles) {
    sanitizeRecursiveNameTree(embeddedFiles, this::sanitizeEmbeddedFile);
  }

  private void sanitizeEmbeddedFile(PDComplexFileSpecification fileSpec) {
    log.trace("Embedded file found: {}", fileSpec.getFilename());

    fileSpec.setEmbeddedFile(sanitizeEmbeddedFile(fileSpec.getEmbeddedFile()));
    fileSpec.setEmbeddedFileUnicode(sanitizeEmbeddedFile(fileSpec.getEmbeddedFileUnicode()));
  }

  private PDEmbeddedFile sanitizeEmbeddedFile(PDEmbeddedFile file) {
    if (file == null) {
      return null;
    }

    log.debug("Sanitizing file: Size: {}, Mime-Type: {}, ", file.getSize(), file.getSubtype());

    ByteArrayInputStream is;
    try {
      is = new ByteArrayInputStream(file.toByteArray());
    } catch (IOException e) {
      log.error("Error during original's file read", e);
      return null;
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      PdfCdr.sanitize(is, os);
    } catch (Exception e) {
      log.error("Error during the embedded file processing", e);
      return null;
    }

    ByteArrayInputStream fakeFile = new ByteArrayInputStream(os.toByteArray());

    PDEmbeddedFile ef;
    try {
      ef = new PDEmbeddedFile(doc, fakeFile, COSName.FLATE_DECODE);
      ef.setCreationDate(file.getCreationDate());
      ef.setModDate(file.getModDate());
    } catch (IOException e) {
      log.error("Error when creating the new sane file", e);
      return null;
    }

    // We copy the properties of the real embedded file
    ef.setSubtype(file.getSubtype());
    ef.setSize(os.size());
    ef.setMacCreator(file.getMacCreator());
    ef.setMacResFork(file.getMacResFork());
    ef.setMacSubtype(file.getMacSubtype());

    // We remove the real file
    file.setSize(0);
    file.setFile(null);

    try {
      // And we empty it
      file.createOutputStream().close();
    } catch (IOException e) {
      log.error("Error when trying to empty the original embedded file", e);
      // Not severe, don't abort operations.
    }
    return ef;
  }

  private <T extends COSObjectable> void sanitizeRecursiveNameTree(PDNameTreeNode<T> efTree,
      Consumer<T> callback) {
    if (efTree == null) {
      return;
    }

    Map<String, T> _names;
    try {
      _names = efTree.getNames();
    } catch (IOException e) {
      log.error("Error in sanitizeRecursiveNameTree", e);
      return;
    }

    if (_names != null) {
      _names.values().forEach(callback);
    }
    if (efTree.getKids() == null) {
      return;
    }
    for (PDNameTreeNode<T> node : efTree.getKids()) {
      sanitizeRecursiveNameTree(node, callback);
    }
  }

}
