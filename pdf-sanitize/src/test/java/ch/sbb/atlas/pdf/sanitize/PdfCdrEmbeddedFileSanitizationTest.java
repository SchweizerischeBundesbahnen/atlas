package ch.sbb.atlas.pdf.sanitize;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.junit.jupiter.api.Test;

class PdfCdrEmbeddedFileSanitizationTest {

  @Test
  void shouldDropEmbeddedFileWhenMaxRecursionDepthReached() throws IOException {
    try (PDDocument doc = new PDDocument()) {
      PDComplexFileSpecification fileSpec = createFileSpecificationWithEmbeddedPdf(doc);
      PDEmbeddedFilesNameTreeNode embeddedFiles = createEmbeddedFilesTree(fileSpec);

      // when: recursion depth already at the maximum
      new PdfCdrEmbeddedFileSanitization(doc, PdfCdr.MAX_RECURSION_DEPTH).sanitize(embeddedFiles);

      // then: embedded file is dropped to prevent infinite recursion
      assertThat(fileSpec.getEmbeddedFile()).isNull();
    }
  }

  @Test
  void shouldSanitizeEmbeddedFileBelowMaxRecursionDepth() throws IOException {
    try (PDDocument doc = new PDDocument()) {
      PDComplexFileSpecification fileSpec = createFileSpecificationWithEmbeddedPdf(doc);
      PDEmbeddedFile originalEmbeddedFile = fileSpec.getEmbeddedFile();
      PDEmbeddedFilesNameTreeNode embeddedFiles = createEmbeddedFilesTree(fileSpec);

      // when: recursion depth below the maximum
      new PdfCdrEmbeddedFileSanitization(doc, PdfCdr.MAX_RECURSION_DEPTH - 1).sanitize(embeddedFiles);

      // then: embedded file is kept but replaced by a sanitized copy
      assertThat(fileSpec.getEmbeddedFile()).isNotNull().isNotSameAs(originalEmbeddedFile);
    }
  }

  private PDComplexFileSpecification createFileSpecificationWithEmbeddedPdf(PDDocument doc) throws IOException {
    byte[] innerPdf = createMinimalPdf();
    PDEmbeddedFile embeddedFile = new PDEmbeddedFile(doc, new ByteArrayInputStream(innerPdf), COSName.FLATE_DECODE);
    embeddedFile.setSubtype("application/pdf");
    embeddedFile.setSize(innerPdf.length);

    PDComplexFileSpecification fileSpec = new PDComplexFileSpecification();
    fileSpec.setFile("inner.pdf");
    fileSpec.setEmbeddedFile(embeddedFile);
    return fileSpec;
  }

  private PDEmbeddedFilesNameTreeNode createEmbeddedFilesTree(PDComplexFileSpecification fileSpec) {
    PDEmbeddedFilesNameTreeNode embeddedFiles = new PDEmbeddedFilesNameTreeNode();
    embeddedFiles.setNames(Map.of("inner.pdf", fileSpec));
    return embeddedFiles;
  }

  private byte[] createMinimalPdf() throws IOException {
    try (PDDocument document = new PDDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      document.addPage(new PDPage());
      document.save(outputStream);
      return outputStream.toByteArray();
    }
  }

  @Test
  void shouldStopNameTreeTraversalWhenMaxTreeDepthReached() throws IOException {
    try (PDDocument doc = new PDDocument()) {
      PDComplexFileSpecification deepFileSpec = createFileSpecificationWithEmbeddedPdf(doc);

      // build a name tree deeper than the allowed maximum, placing the file at the deepest level
      PDEmbeddedFilesNameTreeNode root = createNameTreeChainWithFileAtMaxDepth(deepFileSpec);

      // when: recursion depth for embedded files is fresh, only the tree depth guard should apply
      new PdfCdrEmbeddedFileSanitization(doc, 0).sanitize(root);

      // then: the nested file is still present
      assertThat(deepFileSpec.getEmbeddedFile()).isNotNull();
    }
  }

  private PDEmbeddedFilesNameTreeNode createNameTreeChainWithFileAtMaxDepth(PDComplexFileSpecification deepFileSpec) {
    // The node holding the file sits at tree depth MAX_RECURSION_DEPTH, where traversal is stopped.
    PDEmbeddedFilesNameTreeNode current = createEmbeddedFilesTree(deepFileSpec);
    for (int depth = PdfCdr.MAX_RECURSION_DEPTH - 1; depth >= 0; depth--) {
      PDEmbeddedFilesNameTreeNode parent = new PDEmbeddedFilesNameTreeNode();
      parent.setKids(List.of(current));
      current = parent;
    }
    return current;
  }
}

