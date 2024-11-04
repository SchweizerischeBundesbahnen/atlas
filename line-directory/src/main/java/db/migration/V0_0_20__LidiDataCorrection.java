package db.migration;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

@Slf4j

public class V0_0_20__LidiDataCorrection extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    int count = 0;
    log.info("Starting Lidi Data Correction...");
    try (Statement select = context.getConnection().createStatement()) {
      try (ResultSet rows = select.executeQuery("SELECT slnid, comment FROM line_version WHERE comment like '[%]%'")) {
        while (rows.next()) {
          String slnid = rows.getString(1);
          String comment = rows.getString(2);
          String concessionType = comment.substring(comment.indexOf("[") + 1, comment.indexOf("]"));
          LineConcessionType lineConcessionType = LineConcessionType.from(concessionType);
          if (lineConcessionType != null) {
            String updateQuery = "UPDATE line_version SET concession_type = ? WHERE slnid= ?";
            try (PreparedStatement preparedStatement = context.getConnection().prepareStatement(updateQuery)) {
              preparedStatement.setString(1, lineConcessionType.toString());
              preparedStatement.setString(2, slnid);
              preparedStatement.executeUpdate();
              log.info("Updete LineVersion SLNID: " + slnid + " LineConcessionType: [" + concessionType +
                  "-" + lineConcessionType + "]");
              count++;
            }
          } else {
            log.warn("No Mapping found for ConcessionType [" + concessionType + "] for LineVersion with SLNID[" + slnid + "]");
          }

        }
      }
    }
    log.info("Updated [" + count + "] LineVersions...");
    log.info("Lidi Data Correction ended!");
  }
}
