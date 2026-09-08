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
  private final int recursionDepth;

  PdfCdrEmbeddedFileSanitization(PDDocument doc, int recursionDepth) {
    this.doc = doc;
    this.recursionDepth = recursionDepth;
  }

  void sanitize(PDEmbeddedFilesNameTreeNode embeddedFiles) {
    sanitizeRecursiveNameTree(embeddedFiles, this::sanitizeEmbeddedFile, 0);
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

    if (recursionDepth >= PdfCdr.MAX_RECURSION_DEPTH) {
      log.warn("Reached maximum recursion depth of {} while sanitizing embedded files - dropping embedded file to prevent "
          + "infinite recursion", PdfCdr.MAX_RECURSION_DEPTH);
      return null;
    }

    log.debug("Sanitizing embedded file: size {}, mime-type {}", file.getSize(), file.getSubtype());

    ByteArrayInputStream is;
    try {
      is = new ByteArrayInputStream(file.toByteArray());
    } catch (IOException e) {
      log.error("Error during original's file read", e);
      return null;
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      PdfCdr.sanitize(is, os, recursionDepth + 1);
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
      Consumer<T> callback, int treeDepth) {
    if (efTree == null) {
      return;
    }

    if (treeDepth >= PdfCdr.MAX_RECURSION_DEPTH) {
      log.warn("Reached maximum name tree depth of {} - stopping traversal to prevent infinite recursion",
          PdfCdr.MAX_RECURSION_DEPTH);
      return;
    }

    Map<String, T> names;
    try {
      names = efTree.getNames();
    } catch (IOException e) {
      log.error("Error in sanitizeRecursiveNameTree", e);
      return;
    }

    if (names != null) {
      names.values().forEach(callback);
    }
    if (efTree.getKids() == null) {
      return;
    }
    for (PDNameTreeNode<T> node : efTree.getKids()) {
      sanitizeRecursiveNameTree(node, callback, treeDepth + 1);
    }
  }

}
