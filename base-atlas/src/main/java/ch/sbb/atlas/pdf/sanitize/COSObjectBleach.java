package ch.sbb.atlas.pdf.sanitize;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;

@Slf4j
class COSObjectBleach {

  private void crawl(COSBase base) {
    if (base == null) {
      return;
    }

    if (base instanceof COSName
        || base instanceof COSString
        || base instanceof COSStream
        || base instanceof COSNull
        || base instanceof COSObject
        || base instanceof COSNumber
        || base instanceof COSBoolean) {
      return;
    }

    if (base instanceof COSDictionary dict) {
      Iterator<Entry<COSName, COSBase>> it = dict.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<COSName, COSBase> entry = it.next();
        if ("JS".equals(entry.getKey().getName())
            || "JavaScript".equals(entry.getKey().getName())) {
          it.remove();
          log.debug("Found and removed Javascript code");
          continue;
        }

        if ("S".equals(entry.getKey().getName())) {
          if (entry.getValue() instanceof COSName) {
            if ("JavaScript".equals(((COSName) entry.getValue()).getName())) {
              log.debug("Found and removed Javascript code");
              it.remove();
              continue;
            }
          }
        }

        if ("AA".equals(entry.getKey().getName())) {
          log.debug("Found and removed Additionnal Actions");
          it.remove();
          continue;
        }
        crawl(entry.getValue());
      }
    } else if (base instanceof COSArray ar) {
      for (COSBase item : ar) {
        crawl(item);
      }
    } else {
      log.error("Unknown COS type: {}", base);
    }
  }


  void sanitizeObjects(COSDocument document) {
    log.trace("Checking all objects...");
    // Most destructive operation
    List<COSObject> objectsByType = document.getObjectsByType(COSName.TYPE);
    objectsByType.forEach(this::crawl);
  }
}
