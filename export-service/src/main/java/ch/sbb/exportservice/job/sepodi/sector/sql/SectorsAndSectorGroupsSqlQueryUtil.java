package ch.sbb.exportservice.job.sepodi.sector.sql;

import ch.sbb.exportservice.job.SqlQueryUtil;
import ch.sbb.exportservice.model.ExportTypeV2;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SectorsAndSectorGroupsSqlQueryUtil extends SqlQueryUtil {

  private static final String SELECT_STATEMENT = """
      select * from (
      select s.sloid,
             s.traffic_point_sloid,
             s.valid_from,
             s.valid_to,
             s.designation,
             s.length,
             s.edge_height,
             s.north,
             s.east,
             s.height,
             s.spatial_reference,
             s.creation_date,
             s.creator,
             s.edition_date,
             s.editor,
             string_agg(sgr.sector_group_sloid, '|') as related_groups,
             null                                    as related_sectors,
             'SECTOR' as type
      from sector_version s
      left join sector_group_relations sgr on s.sloid = sgr.sector_sloid
      group by s.id
      union
      select sg.sloid,
             sg.traffic_point_sloid,
             sg.valid_from,
             sg.valid_to,
             sg.designation,
             sg.length,
             null,
             null,
             null,
             null,
             null,
             sg.creation_date,
             sg.creator,
             sg.edition_date,
             sg.editor,
             null                                    as related_groups,
             string_agg(sgr.sector_group_sloid, '|') as related_sectors,
             'SECTOR_GROUP' as type
      from sector_group_version sg
      left join sector_group_relations sgr on sg.sloid = sgr.sector_group_sloid
      group by sg.id
      )""";
  private static final String ORDER_BY_STATEMENT = "order by type, sloid, valid_from ASC";

  public String getSqlQuery(ExportTypeV2 exportTypeV2) {
    String sqlQuery = ExportSqlQueryBuilder.builder()
        .exportType(exportTypeV2)
        .selectStatement(SELECT_STATEMENT)
        .groupByAndOrderByClause(ORDER_BY_STATEMENT)
        .build().getQuery();
    log.info("Execution SQL query:");
    log.info(sqlQuery);
    return sqlQuery;
  }

}
