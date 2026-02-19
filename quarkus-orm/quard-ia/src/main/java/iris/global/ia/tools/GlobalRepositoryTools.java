package iris.global.ia.tools;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.agent.tool.Tool;
import iris.global.models.dto.GlobalSnapshotDTO;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GlobalRepositoryTools {

  @Inject
  EntityManager em;

  @Tool("""
      You are calling an analytical tool that identifies globals with the highest absolute disk growth.

      PURPOSE:
      Return the top globals that experienced the highest absolute disk growth (in MB)
      for a specific snapshot date.

      This tool is optimized for answering questions about:
      - Which globals grew the most on a given day
      - Sudden disk consumption increases
      - Daily growth ranking of globals

      WHEN TO USE:
      - When the user asks which globals grew the most
      - When the user asks about disk growth on a specific date
      - When performing daily or point-in-time growth analysis
      - When investigating unexpected disk usage spikes

      WHEN NOT TO USE:
      - Do NOT use for long-term historical trends
      - Do NOT use if the snapshot date is unknown
      - Do NOT use to compare multiple dates
      - Do NOT guess or infer dates

      PARAMETERS:
      - date (String) [required]
        Description: Snapshot date used to calculate growth.
        Format: ISO-8601 (yyyy-MM-dd).
        How to use: Must represent an existing snapshot date in the repository.
        Example: "2026-02-15"

      RULES:
      - Only call this tool if the date is explicitly known.
      - Never infer or fabricate a date.
      - If the user refers to "today" or "yesterday", resolve it explicitly before calling.
      - If the date cannot be resolved, ask the user for clarification.

      OUTPUT:
      Returns up to 20 globals ordered by absolute growth in MB (descending),
      mapped as GlobalSnapshotDTO.
      """)

  @Nonnull
  @SuppressWarnings("null")
  public List<Map<String, Object>> topGrowingGlobals(String date) {
    LocalDate snapshotDate = LocalDate.parse(date.trim());

    @SuppressWarnings("unchecked")
    List<Object[]> rows = em.createNativeQuery("""
            SELECT TOP 20
                GlobalName,
                GrowthMB,
                GrowthPct,
                UsedMB,
                Location
            FROM guard.GlobalSnapshot
            WHERE SnapshotDate = :dt
            ORDER BY GrowthMB DESC
        """)
        .setParameter("dt", LocalDate.parse(snapshotDate.toString()))
        .getResultList();

    return rows.stream()
        .map(r -> {
          Map<String, Object> m = new HashMap<>();
          m.put("globalName", r[0]);
          m.put("growthMB", r[1]);
          m.put("growthPct", r[2]);
          m.put("usedMB", r[3]);
          m.put("location", r[4]);
          return m;
        })
        .toList();
  }

  @Tool("""
      You are calling a risk assessment tool for IRIS globals.

      PURPOSE:
      Identify globals that represent higher operational risk based on:
      - Minimum growth percentage
      - Minimum disk usage (MB)

      This tool helps detect globals that may cause disk exhaustion or
      require cleanup, archiving, or investigation.

      WHEN TO USE:
      - When the user asks about risky, dangerous, or problematic globals
      - When identifying candidates for cleanup
      - When performing proactive disk risk analysis
      - When growth rate and size must be evaluated together

      WHEN NOT TO USE:
      - Do NOT use for absolute growth ranking
      - Do NOT use for single-global analysis
      - Do NOT use if thresholds are not explicitly defined

      PARAMETERS:
      - minGrowthPct (double) [required]
        Description: Minimum growth percentage threshold.
        How to use: Value must represent percent growth (not fraction).
        Example: 15.0 means 15% growth.

      - minUsedMB (double) [required]
        Description: Minimum disk usage threshold in MB.
        How to use: Used to filter out small or insignificant globals.
        Example: 500.0

      RULES:
      - Only call this tool if both thresholds are explicitly known.
      - Never invent threshold values.
      - If the user says "high growth" or "large globals" without numbers, ask for clarification.
      - Do not normalize or auto-scale values.

      OUTPUT:
      Returns a list of globals ordered by growth percentage (descending),
      mapped as GlobalSnapshotDTO.
      """)
  @Nonnull
  @SuppressWarnings("null")
  public List<GlobalSnapshotDTO> riskyGlobals(
      double minGrowthPct,
      double minUsedMB) {

    @SuppressWarnings("unchecked")
    List<Object[]> rows = em.createNativeQuery("""
            SELECT
                CAST(ID AS BIGINT),
                AllocatedMB,
                GlobalName,
                GrowthMB,
                GrowthPct,
                Location,
                CAST(PrevSnapshotId AS BIGINT),
                CAST(SnapshotDate AS DATE),
                Tables,
                UsedMB
            FROM guard.GlobalSnapshot
            WHERE GrowthPct >= :minGrowthPct
            AND UsedMB >= :minUsedMB
            ORDER BY GrowthPct DESC
        """)
        .setParameter("minGrowthPct", minGrowthPct)
        .setParameter("minUsedMB", minUsedMB)
        .getResultList();

    return rows.stream()
        .map(row -> new GlobalSnapshotDTO(
            (Long) row[0],
            row[1] == null ? null : new BigDecimal(row[1].toString()),
            (String) row[2],
            row[3] == null ? null : new BigDecimal(row[3].toString()),
            row[4] == null ? null : new BigDecimal(row[4].toString()),
            (String) row[5],
            (Long) row[6],
            (LocalDate) row[7],
            (String) row[8],
            row[9] == null ? null : new BigDecimal(row[9].toString())
        ))
        .toList();
  }

  @Tool("""
      You are calling a historical analysis tool for a single IRIS global.

      PURPOSE:
      Return the full historical growth timeline of a specific global,
      ordered chronologically.

      This tool is designed for long-term analysis, anomaly detection,
      and understanding when abnormal growth started.

      WHEN TO USE:
      - When the user asks about historical growth of a global
      - When analyzing long-term or seasonal behavior
      - When investigating when a global started growing abnormally
      - When performing root cause analysis

      WHEN NOT TO USE:
      - Do NOT use for ranking or comparison between globals
      - Do NOT use if the global name is unknown
      - Do NOT use for snapshot-only analysis

      PARAMETERS:
      - globalName (String) [required]
        Description: Name of the global to analyze.
        How to use:
          - Can be provided with or without '^'
          - Will be normalized internally
        Example: "^MyGlobal", "MyGlobal"

      RULES:
      - Only call this tool if the global name is explicitly known.
      - Never guess or infer a global name.
      - If the user refers to a global indirectly, ask for confirmation.

      OUTPUT:
      Returns a chronological list of GlobalSnapshotDTO entries
      representing the full growth history of the global.
      """)
  @Nonnull
  @SuppressWarnings("null")
  public List<GlobalSnapshotDTO> globalHistory(String globalName) {

    String normalized = globalName.trim()
        .toUpperCase()
        .replaceFirst("^\\^", "");

    @SuppressWarnings("unchecked")
    List<Object[]> rows = em.createNativeQuery("""
            SELECT
                CAST(ID AS BIGINT),
                AllocatedMB,
                GlobalName,
                GrowthMB,
                GrowthPct,
                Location,
                CAST(PrevSnapshotId AS BIGINT),
                CAST(SnapshotDate AS DATE),
                Tables,
                UsedMB
            FROM guard.GlobalSnapshot
            WHERE GlobalName = :globalName
            ORDER BY SnapshotDate ASC
        """)
        .setParameter("globalName", normalized)
        .getResultList();

    return rows.stream()
        .map(row -> new GlobalSnapshotDTO(
            (Long) row[0],
            row[1] == null ? null : new BigDecimal(row[1].toString()),
            (String) row[2],
            row[3] == null ? null : new BigDecimal(row[3].toString()),
            row[4] == null ? null : new BigDecimal(row[4].toString()),
            (String) row[5],
            (Long) row[6],
            (LocalDate) row[7],
            (String) row[8],
            row[9] == null ? null : new BigDecimal(row[9].toString())
        ))
        .toList();
  }

  @Tool("""
      You are calling an aggregation tool that summarizes disk usage and growth
      by database location.

      PURPOSE:
      Aggregate disk usage and growth metrics by database location
      for a specific snapshot date.

      This tool provides a location-level view of disk consumption and growth,
      useful for capacity planning and storage monitoring.

      WHEN TO USE:
      - Get information about disks location-level growth and usage
      - When the user asks about disk usage per database or location
      - When comparing growth across storage locations
      - When analyzing which disk or database is consuming more space
      - When performing capacity or infrastructure-level analysis

      WHEN NOT TO USE:
      - Do NOT use for per-global analysis
      - Do NOT use without a known snapshot date
      - Do NOT use for historical trends across multiple dates

      PARAMETERS:
      - snapshotDate (LocalDate) [required]
        Description: Snapshot date used for aggregation.
        How to use: Must correspond to an existing snapshot.
        Example: 2026-02-15

      RULES:
      - Only call this tool if the snapshot date is explicitly known.
      - Never invent or assume dates.
      - If the user refers to relative dates, resolve them explicitly first.

      OUTPUT:
      Returns a map where:
      - key   = database location
      - value = aggregated metrics:
                usedMB, allocatedMB, growthMB
      """)
  @Nonnull
  public Map<String, Map<String, BigDecimal>> diskSUMGrowthByLocation(
      LocalDate snapshotDate) {

    @SuppressWarnings("unchecked")
    List<Object[]> rows = em.createNativeQuery("""
        SELECT
                Location,
                SUM(UsedMB),
                SUM(AllocatedMB),
                SUM(GrowthMB)
            FROM guard.GlobalSnapshot
            WHERE CAST(SnapshotDate AS DATE) = :dt
            GROUP BY Location
        """)
        .setParameter("dt", snapshotDate)
        .getResultList();

    Map<String, Map<String, BigDecimal>> result = new HashMap<>();

    for (Object[] row : rows) {
      String location = (String) row[0];

      BigDecimal usedMB = row[1] == null ? null : new BigDecimal(row[1].toString());
      BigDecimal allocatedMB = row[2] == null ? null : new BigDecimal(row[2].toString());
      BigDecimal growthMB = row[3] == null ? null : new BigDecimal(row[3].toString());

      Map<String, BigDecimal> metrics = new HashMap<>();
      metrics.put("usedMB", usedMB);
      metrics.put("allocatedMB", allocatedMB);
      metrics.put("growthMB", growthMB);

      result.put(location, metrics);
    }

    return result;
  }

}
